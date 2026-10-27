package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingCreateDocumentService {
    
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final BuyOutSurplusFacilityDocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    
    public CompletableFuture<FileInfoDTO> create(final Request request, 
            final TargetPeriodType targetPeriodType, 
            final BuyOutSurplusChargeType chargeType,
            final Map<Long, FacilityAddressDTO> facilityAddressesById) {
        final BuyOutSurplusFacilityAccountProcessingRequestPayload payload = (BuyOutSurplusFacilityAccountProcessingRequestPayload) request.getPayload();
        final BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = (BuyOutSurplusFacilityAccountProcessingRequestMetadata) request.getMetadata();
        final TargetPeriodInfoDTO currentTargetPeriod = payload.getTargetPeriodsDetails().getLast();
               
        final String transactionCode = chargeType.equals(BuyOutSurplusChargeType.FEE)
                ? metadata.getBuyOutTransactionCodes().get(targetPeriodType)
                : metadata.getRefundTransactionCodes().get(targetPeriodType);
        
        return ccaFileDocumentGeneratorService.generateAsync(request,
                payload.getSubmitterId(),
                getTemplateType(payload.isApplyPrimaryRules(), currentTargetPeriod.getBusinessId(), targetPeriodType, chargeType),
                constructTemplateParams(request, targetPeriodType, chargeType, facilityAddressesById),
                constructFileName(payload.isApplyPrimaryRules(), currentTargetPeriod.getBusinessId(), targetPeriodType, chargeType, transactionCode));
    }
    
    private TemplateParams constructTemplateParams(final Request request, 
            final TargetPeriodType targetPeriodType, 
            final BuyOutSurplusChargeType chargeType,
            final Map<Long, FacilityAddressDTO> facilityAddressesById) {

        TemplateParams documentTemplateParams = new TemplateParams();
        documentTemplateParams.getParams().putAll(documentTemplateTransformationMapper
                .constructTemplateParams(request, targetPeriodType, chargeType, facilityAddressesById));

        return documentTemplateParams;
    }
    
    private String getTemplateType(final boolean applyPrimaryRules, 
            final TargetPeriodType currentTargetPeriodType,
            final TargetPeriodType targetPeriodType,
            final BuyOutSurplusChargeType chargeType) {

        if (applyPrimaryRules && currentTargetPeriodType.equals(targetPeriodType)) {
            return CcaDocumentTemplateType.PRIMARY_BUY_OUT_CCA3;
        }

        return switch (chargeType) {
        case FEE -> CcaDocumentTemplateType.SECONDARY_BUY_OUT_CCA3;
        case REFUND -> CcaDocumentTemplateType.SECONDARY_OVERPAYMENT_BUY_OUT_CCA3;
        };
    }

    private String constructFileName(final boolean applyPrimaryRules, 
            final TargetPeriodType currentTargetPeriodType,
            final TargetPeriodType targetPeriodType,
            final BuyOutSurplusChargeType chargeType,
            final String transactionCode) {

        if (applyPrimaryRules && currentTargetPeriodType.equals(targetPeriodType)) {
            return transactionCode + " Primary buy-out MoA.pdf";
        }

        return switch (chargeType) {
        case FEE -> transactionCode + " Secondary buy-out MoA.pdf";
        case REFUND -> transactionCode + " Secondary buy-out overpayment letter.pdf";
        };
    }
}
