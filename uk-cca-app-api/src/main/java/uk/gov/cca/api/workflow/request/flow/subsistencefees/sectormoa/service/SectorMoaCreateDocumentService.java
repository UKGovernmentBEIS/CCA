package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.utils.SubsistenceFeesUtility;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.Year;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SectorMoaCreateDocumentService {

    private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final SubsistenceFeesConfig subsistenceFeesConfig;

    public CompletableFuture<FileInfoDTO> create(final Request request, final List<EligibleFacilityDTO> facilities) {
        final SectorMoaRequestMetadata metadata = (SectorMoaRequestMetadata) request.getMetadata();
        final SectorMoaRequestPayload payload = (SectorMoaRequestPayload) request.getPayload();
        final Request parentRequest = requestService.findRequestById(metadata.getParentRequestId());
        final SubsistenceFeesRunRequestMetadata parentMetadata = (SubsistenceFeesRunRequestMetadata) parentRequest.getMetadata();
        final SubsistenceFeesRunRequestPayload parentPayload = (SubsistenceFeesRunRequestPayload) parentRequest.getPayload();

        final Long sectorAssociationId = payload.getSectorAssociationId();
        final Year chargingYear = parentMetadata.getChargingYear();
        final String signatory = parentPayload.getSubmitterId();
        final String sectorAcronym = metadata.getSectorAcronym();
        final String transactionId = metadata.getTransactionId();

        return ccaFileDocumentGeneratorService.generateAsync(signatory, sectorAssociationId,
                CcaDocumentTemplateType.SECTOR_MOA,
                SubsistenceFeesUtility.constructSectorMoaTemplateParams(transactionId, facilities, chargingYear.getValue(), subsistenceFeesConfig.getFacilityFee()),
                constructFileName(chargingYear, sectorAcronym, transactionId));
    }

    private String constructFileName(final Year chargingYear, final String sectorAcronym, final String transactionId) {
        return chargingYear +
                " Sector MoA - " +
                sectorAcronym +
                " - " +
                transactionId +
                ".pdf";
    }


}
