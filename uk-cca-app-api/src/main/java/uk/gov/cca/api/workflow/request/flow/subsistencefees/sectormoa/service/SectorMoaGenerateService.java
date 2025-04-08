package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesTransactionIdGeneratorService;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaFacilitiesService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.validation.SubsistenceFeesViolation;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.transform.SectorMoaMapper;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorMoaGenerateService {

    private final RequestService requestService;
    private final SectorMoaGenerateDocumentsService sectorMoaGenerateDocumentsService;
    private final SectorMoaOfficialNoticeService sectorMoaOfficialNoticeService;
    private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
    private final SubsistenceFeesTransactionIdGeneratorService transactionIdGeneratorService;
    private final SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;
    private final MoaFacilitiesService facilitiesService;
    private final CcaOfficialNoticeSendService officialNoticeSendService;
    private static final SectorMoaMapper SECTOR_MOA_MAPPER = Mappers.getMapper(SectorMoaMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void generateMoa(final String requestId) throws BpmnExecutionException {
        try {
            final Request request = requestService.findRequestById(requestId);
            final SectorMoaRequestMetadata metadata = (SectorMoaRequestMetadata) request.getMetadata();
            final SectorMoaRequestPayload payload = (SectorMoaRequestPayload) request.getPayload();

            final Request parentRequest = requestService.findRequestById(metadata.getParentRequestId());
            final SubsistenceFeesRunRequestMetadata parentMetadata = (SubsistenceFeesRunRequestMetadata) parentRequest.getMetadata();
            final SubsistenceFeesRunRequestPayload parentPayload = (SubsistenceFeesRunRequestPayload) parentRequest.getPayload();

            final Year chargingYear = parentMetadata.getChargingYear();
            final Long sectorAssociationId = payload.getSectorAssociationId();
            final List<EligibleFacilityDTO> facilities =
                    subsistenceFeesRunQueryService.getSectorEligibleFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear);

            if (facilities.isEmpty()) {
                throw new BusinessException(CcaErrorCode.SECTOR_MOA_CONTAINS_NO_FACILITIES);
            }

            final String transactionId = transactionIdGeneratorService.generateTransactionIdForSectorMOAs();
            metadata.setTransactionId(transactionId);
            request.setSubmissionDate(LocalDateTime.now());

            // Generate sector MoA documents
            sectorMoaGenerateDocumentsService.generateDocuments(request, facilities);

            // Persist sector MOA business objects
            final String documentUuid = payload.getSectorMoaDocument().getUuid();
            final Long runId = parentPayload.getRunId();
            subsistenceFeesRunUpdateService.persistMoa(sectorAssociationId, transactionId, runId, MoaType.SECTOR_MOA, facilities, documentUuid);

            // Flag facilities included in the moa
            facilitiesService.flagMoaFacilities(runId, chargingYear, MoaType.SECTOR_MOA, facilities);

            // Add timeline event
            final List<DefaultNoticeRecipient> recipients = officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request);
            final SectorMoaGeneratedRequestActionPayload requestActionPayload =
                    SECTOR_MOA_MAPPER.toGeneratedActionPayload(payload, metadata, chargingYear, recipients);

            requestService.addActionToRequest(request,
                    requestActionPayload,
                    CcaRequestActionType.SECTOR_MOA_GENERATED,
                    parentPayload.getSubmitterId());

            // Send sector MoA notice
            sectorMoaOfficialNoticeService.sendOfficialNotice(request);
        } catch (Exception e) {
            throw new BpmnExecutionException(
                    SubsistenceFeesViolation.SubsistenceFeesViolationMessage.GENERATE_SECTOR_MOA_FAILED.getMessage(),
                    List.of(e.getMessage()));
        }
    }
}
