package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

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
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaFacilitiesService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.validation.SubsistenceFeesViolation;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.transform.TargetUnitMoaMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetUnitMoaGenerateService {

    private final RequestService requestService;
    private final TargetUnitMoaGenerateDocumentsService targetUnitMoaGenerateDocumentsService;
    private final TargetUnitMoaOfficialNoticeService targetUnitMoaOfficialNoticeService;
    private final SubsistenceFeesTransactionIdGeneratorService transactionIdGeneratorService;
    private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
    private final SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;
    private final MoaFacilitiesService facilitiesService;
    private final CcaOfficialNoticeSendService officialNoticeSendService;
    private static final TargetUnitMoaMapper TARGET_UNIT_MOA_MAPPER = Mappers.getMapper(TargetUnitMoaMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void generateMoa(final String requestId) throws BpmnExecutionException {
        try {
            final Request request = requestService.findRequestById(requestId);
            final TargetUnitMoaRequestMetadata metadata = (TargetUnitMoaRequestMetadata) request.getMetadata();
            final TargetUnitMoaRequestPayload payload = (TargetUnitMoaRequestPayload) request.getPayload();

            final Request parentRequest = requestService.findRequestById(metadata.getParentRequestId());
            final SubsistenceFeesRunRequestMetadata parentMetadata = (SubsistenceFeesRunRequestMetadata) parentRequest.getMetadata();
            final SubsistenceFeesRunRequestPayload parentPayload = (SubsistenceFeesRunRequestPayload) parentRequest.getPayload();

            final Year chargingYear = parentMetadata.getChargingYear();
            final Long accountId = request.getAccountId();
            final List<EligibleFacilityDTO> facilities =
                    subsistenceFeesRunQueryService.getAccountEligibleFacilitiesForSubsistenceFeesRun(accountId, chargingYear);

            if (facilities.isEmpty()) {
                throw new BusinessException(CcaErrorCode.TARGET_UNIT_MOA_CONTAINS_NO_FACILITIES);
            }

            final String transactionId = transactionIdGeneratorService.generateTransactionIdForTargetUnitMOAs();
            metadata.setTransactionId(transactionId);
            request.setSubmissionDate(LocalDateTime.now());

            // Generate TU MoA documents
            targetUnitMoaGenerateDocumentsService.generateDocuments(request, facilities);

            // Persist TU MOA business objects
            final String documentUuid = payload.getTargetUnitMoaDocument().getUuid();
            final Long runId = parentPayload.getRunId();
            subsistenceFeesRunUpdateService.persistMoa(accountId, transactionId, runId, MoaType.TARGET_UNIT_MOA, facilities, documentUuid);

            // Flag facilities included in the moa
            facilitiesService.flagMoaFacilities(runId, chargingYear, MoaType.TARGET_UNIT_MOA, facilities);

            // Add timeline event
            final List<DefaultNoticeRecipient> recipients = officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request);
            final TargetUnitMoaGeneratedRequestActionPayload requestActionPayload =
                    TARGET_UNIT_MOA_MAPPER.toGeneratedActionPayload(payload, metadata, chargingYear, recipients);

            requestService.addActionToRequest(request,
                    requestActionPayload,
                    CcaRequestActionType.TARGET_UNIT_MOA_GENERATED,
                    parentPayload.getSubmitterId());

            // Send TU MoA notice
            targetUnitMoaOfficialNoticeService.sendOfficialNotice(request);
        } catch (final Exception e) {
            throw new BpmnExecutionException(
                    SubsistenceFeesViolation.SubsistenceFeesViolationMessage.GENERATE_TARGET_UNIT_MOA_FAILED.getMessage(),
                    e.getMessage() != null ? List.of(e.getMessage()) : Collections.emptyList());
        }
    }
}
