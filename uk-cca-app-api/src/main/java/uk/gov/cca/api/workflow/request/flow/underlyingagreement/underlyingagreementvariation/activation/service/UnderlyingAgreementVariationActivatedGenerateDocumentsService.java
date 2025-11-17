package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementCalculateSchemeVersionsUtil;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedGenerateDocumentsService {

    private final RequestService requestService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final UnderlyingAgreementVariationCreateDocumentService underlyingAgreementVariationCreateDocumentService;
    private final UnderlyingAgreementVariationOfficialNoticeService underlyingAgreementVariationOfficialNoticeService;

    @Transactional
    public void generateDocuments(String requestId) {
        Map<SchemeVersion, CompletableFuture<FileInfoDTO>> underlyingAgreementDocumentFutureMap = new EnumMap<>(SchemeVersion.class);
        List<CompletableFuture<FileInfoDTO>> officialNoticeFutureList = new ArrayList<>();
        CompletableFuture<Void> allFutures = null;

        try {
            final Request request = requestService.findRequestById(requestId);
            final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
            final Set<Facility> facilities = requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities();

            // Get versions of documents to be generated from proposed
            Set<SchemeVersion> activeSchemeVersions = UnderlyingAgreementCalculateSchemeVersionsUtil
                    .calculateSchemeVersionsFromActiveFacilities(facilities);
            activeSchemeVersions.forEach(version -> underlyingAgreementDocumentFutureMap.put(version,
                    underlyingAgreementVariationCreateDocumentService.create(requestId, version)));

            // Generate official notices
            officialNoticeFutureList.add(underlyingAgreementVariationOfficialNoticeService
                    .generateActivatedOfficialNotice(requestId));

            // Find terminated documents from facilities
            Set<SchemeVersion> terminatedVersions = UnderlyingAgreementCalculateSchemeVersionsUtil
                    .calculateTerminatedSchemeVersionsFromFacilities(facilities);
            terminatedVersions.forEach(version ->
                    officialNoticeFutureList.add(underlyingAgreementVariationOfficialNoticeService
                            .generateTerminationOfficialNotice(requestId, version)));

            // Complete futures
            List<CompletableFuture<?>> allFuturesList = new ArrayList<>(underlyingAgreementDocumentFutureMap.values());
            allFuturesList.addAll(officialNoticeFutureList);

            allFutures = CompletableFuture.allOf(allFuturesList.toArray(CompletableFuture[]::new));
            allFutures.get();

            final Map<SchemeVersion, FileInfoDTO> underlyingAgreementDocumentsMap = new EnumMap<>(SchemeVersion.class);
            for (Map.Entry<SchemeVersion, CompletableFuture<FileInfoDTO>> entry : underlyingAgreementDocumentFutureMap.entrySet()) {
                underlyingAgreementDocumentsMap.put(entry.getKey(), entry.getValue().get());
            }

            final List<FileInfoDTO> officialNotices = new ArrayList<>();
            for (CompletableFuture<FileInfoDTO> notice : officialNoticeFutureList) {
                officialNotices.add(notice.get());
            }

            // Update request payload
            requestPayload.setUnderlyingAgreementDocuments(underlyingAgreementDocumentsMap);
            requestPayload.setOfficialNotices(officialNotices);

            // Update UNA documents
            UnderlyingAgreementDTO underlyingAgreementDTO = underlyingAgreementQueryService
                    .getUnderlyingAgreementByAccountId(request.getAccountId());
            underlyingAgreementDocumentsMap.forEach((key, value) -> underlyingAgreementService.saveFileDocumentUuid(
                    getDocumentIdForSchemeVersion(underlyingAgreementDTO, key), value.getUuid()));

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
            List<CompletableFuture<?>> allFuturesList = new ArrayList<>(underlyingAgreementDocumentFutureMap.values());
            allFuturesList.addAll(officialNoticeFutureList);
            allFuturesList.add(allFutures);
            ConcurrencyUtils.completeCompletableFutures(allFuturesList.toArray(CompletableFuture[]::new));
        }
    }
    
    private Long getDocumentIdForSchemeVersion(final UnderlyingAgreementDTO underlyingAgreementDTO, SchemeVersion schemeVersion) {
		return underlyingAgreementDTO.getUnderlyingAgreementDocuments().stream()
				.filter(doc -> schemeVersion.equals(doc.getSchemeVersion()))
				.findFirst()
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND))
				.getId();
	}
}
