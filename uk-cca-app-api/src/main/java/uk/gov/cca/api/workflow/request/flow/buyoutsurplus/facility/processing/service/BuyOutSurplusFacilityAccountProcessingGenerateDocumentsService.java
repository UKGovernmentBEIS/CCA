package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.facility.transform.FacilityAddressMapper;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionKey;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityResultsUtil;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentUnprocessedGeneratorService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingGenerateDocumentsService {

    private final FacilityDataQueryService facilityDataQueryService;
    private final BuyOutSurplusFacilityAccountProcessingCreateDocumentService createDocumentService;
    private final CcaFileDocumentUnprocessedGeneratorService ccaFileDocumentUnprocessedGeneratorService;
    private static final FacilityAddressMapper FACILITY_ADDRESS_MAPPER =  Mappers.getMapper(FacilityAddressMapper.class);
    
    public void generateDocuments(Request request) {       
        final BuyOutSurplusFacilityAccountProcessingRequestPayload payload = (BuyOutSurplusFacilityAccountProcessingRequestPayload) request.getPayload();
        final BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = (BuyOutSurplusFacilityAccountProcessingRequestMetadata) request.getMetadata();
        CompletableFuture<Void> allFutures = null;
        Map<BuyOutSurplusFacilityTransactionKey, CompletableFuture<FileInfoDTO>> transactionDocumentFutureMap = new HashMap<>();
                
        try {
            
            Map<Long, FacilityAddressDTO> facilityAddressesById = getFacilityAddressesById(payload);
            
            if (!ObjectUtils.isEmpty(metadata.getBuyOutTransactionCodes())) {
                payload.setBuyOutOfficialNotices(new EnumMap<>(TargetPeriodType.class));
                metadata.getBuyOutTransactionCodes().forEach((key, value) -> transactionDocumentFutureMap.put(
                        new BuyOutSurplusFacilityTransactionKey(key, BuyOutSurplusChargeType.FEE),
                        createDocumentService.create(request, key, BuyOutSurplusChargeType.FEE, facilityAddressesById))); 
            }
            
            if (!ObjectUtils.isEmpty(metadata.getRefundTransactionCodes())) {
                payload.setRefundOfficialNotices(new EnumMap<>(TargetPeriodType.class));
                metadata.getRefundTransactionCodes().forEach((key, value) -> transactionDocumentFutureMap.put(
                        new BuyOutSurplusFacilityTransactionKey(key, BuyOutSurplusChargeType.REFUND),
                        createDocumentService.create(request, key, BuyOutSurplusChargeType.REFUND, facilityAddressesById)));   
            }

            List<CompletableFuture<?>> allFuturesList = new ArrayList<>(transactionDocumentFutureMap.values());
 
            allFutures = CompletableFuture.allOf(allFuturesList.toArray(CompletableFuture[]::new));
            allFutures.get();
            
            for (Map.Entry<BuyOutSurplusFacilityTransactionKey, CompletableFuture<FileInfoDTO>> entry : transactionDocumentFutureMap.entrySet()) {
                
                if (entry.getKey().chargeType().equals(BuyOutSurplusChargeType.FEE)) {
                    payload.getBuyOutOfficialNotices().put(entry.getKey().targetPeriodType(), entry.getValue().get());
                } else {
                    payload.getRefundOfficialNotices().put(entry.getKey().targetPeriodType(), entry.getValue().get());
                }
            }
            
            if (!ObjectUtils.isEmpty(metadata.getRefundTransactionCodes())) {
                FileInfoDTO refundClaimForm = ccaFileDocumentUnprocessedGeneratorService.copyTemplateAndSaveFileDocument(
                        CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                        CompetentAuthorityEnum.valueOf(request.getRequestResourcesMap().get(ResourceType.CA)),
                        "Buyout Refund Claim Form.docx");
                payload.setRefundClaimForm(refundClaimForm);
            }
            
        } catch (BusinessException e) {
            throw e;
        } catch (ExecutionException e) {
            Throwable caused = e.getCause();
            if (caused.getClass() == BusinessException.class) {
                throw (BusinessException) caused;
            } else {
                log.error(caused.getMessage());
                throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
            }
        } catch (InterruptedException e) {
            Throwable caused = e.getCause();
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
        } catch (Exception e) {
            Throwable caused = e.getCause();
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
        } finally {
            List<CompletableFuture<?>> allFuturesList = new ArrayList<>(transactionDocumentFutureMap.values());
            allFuturesList.add(allFutures);
            ConcurrencyUtils.completeCompletableFutures(allFuturesList.toArray(CompletableFuture[]::new));
        }
    }

    private Map<Long, FacilityAddressDTO> getFacilityAddressesById(
            final BuyOutSurplusFacilityAccountProcessingRequestPayload payload) {
        
        List<Long> facilityIds = payload.getFacilityPerformanceResults().stream()
                .filter(BuyOutSurplusFacilityResultsUtil::isTransactionRequired)
                .map(BuyOutSurplusFacilityPerformanceResult::getFacilityId)
                .distinct()
                .toList();

       return facilityDataQueryService.getFacilityDataByIds(facilityIds)
                .stream()
                .collect(Collectors.toMap(
                        FacilityData::getId,
                        fac -> FACILITY_ADDRESS_MAPPER.toFacilityAddressDTO(fac.getAddress())
                ));
    }
}
