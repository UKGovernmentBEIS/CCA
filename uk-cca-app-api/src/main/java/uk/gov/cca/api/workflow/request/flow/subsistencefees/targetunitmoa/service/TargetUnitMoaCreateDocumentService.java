package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.utils.SubsistenceFeesUtility;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.Year;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class TargetUnitMoaCreateDocumentService {

    private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final SubsistenceFeesConfig subsistenceFeesConfig;

    public CompletableFuture<FileInfoDTO> create(final Request request, List<EligibleFacilityDTO> facilities) {
        TargetUnitMoaRequestMetadata metadata = (TargetUnitMoaRequestMetadata) request.getMetadata();
        final String businessId = metadata.getBusinessId();
        final String transactionId = metadata.getTransactionId();

        final Request parentRequest = requestService.findRequestById(metadata.getParentRequestId());
        SubsistenceFeesRunRequestMetadata parentMetadata = (SubsistenceFeesRunRequestMetadata) parentRequest.getMetadata();
        SubsistenceFeesRunRequestPayload parentPayload = (SubsistenceFeesRunRequestPayload) parentRequest.getPayload();
        final Year chargingYear = parentMetadata.getChargingYear();
        final String signatory = parentPayload.getSubmitterId();

        return ccaFileDocumentGeneratorService.generateAsync(request, signatory,
                CcaDocumentTemplateType.TARGET_UNIT_MOA,
                SubsistenceFeesUtility.constructTargetUnitMoaTemplateParams(transactionId, facilities, chargingYear.getValue(), subsistenceFeesConfig.getFacilityFee()),
                constructFileName(chargingYear, businessId, transactionId));
    }

    private String constructFileName(final Year chargingYear, final String businessId, final String transactionId) {
        return chargingYear +
                " Target Unit MoA - " +
                businessId +
                " - " +
                transactionId +
                ".pdf";
    }
}
