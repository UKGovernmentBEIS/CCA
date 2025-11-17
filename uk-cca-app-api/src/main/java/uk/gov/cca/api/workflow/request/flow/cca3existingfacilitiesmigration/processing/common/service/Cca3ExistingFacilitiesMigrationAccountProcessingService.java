package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.utils.Cca3ExistingFacilitiesMigrationUtil;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.transform.Cca3ExistingFacilitiesMigrationAccountProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.validation.Cca3ExistingFacilitiesMigrationAccountProcessingValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingService {

    private final RequestService requestService;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private final Cca3ExistingFacilitiesMigrationAccountProcessingValidator cca3ExistingFacilitiesMigrationAccountProcessingValidator;
    private final Cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService;
    private final Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final Cca3ExistingFacilitiesMigrationAccountProcessingMapper MAPPER = Mappers
            .getMapper(Cca3ExistingFacilitiesMigrationAccountProcessingMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(final String requestId, final Cca3FacilityMigrationAccountState accountState) throws BpmnExecutionException {
        try {
            final Request request = requestService.findRequestById(requestId);
            final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                    (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

            // Validate facilities
            cca3ExistingFacilitiesMigrationAccountProcessingValidator.validate(accountState, payload);

            if(!accountState.getErrors().isEmpty()) {
                return;
            }

            // Process
            if(accountState.isCca3Participating()) {
                processAccountWithCca3Facilities(request);
            }
            else {
                processAccountWithOnlyCca2Facilities(request);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(e.getMessage()));
        }
    }

    private void processAccountWithCca3Facilities(final Request request) throws IOException {
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

        // Set calculator files with status SUBMITTED and create placeholder files
        updateAttachments(payload.getFacilityMigrationDataList());

        // Update Una facilities
        updateFacilities(payload);

        // Get Default notice contacts
        List<DefaultNoticeRecipient> defaultContacts = ccaOfficialNoticeSendService
                .getOfficialNoticeToDefaultRecipients(request);

        // Generate official notice and UNA document
        cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService.generateDocuments(request.getId());

        // Create request action
        createSubmittedRequestAction(request, defaultContacts);

        // Send notification notice
        cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.sendOfficialNotice(request.getId());
    }

    private void processAccountWithOnlyCca2Facilities(final Request request) {
        // Create request action
        createSubmittedRequestAction(request, List.of());

        // Workflow is completed set submission date
        request.setSubmissionDate(LocalDateTime.now());
    }

    private void createSubmittedRequestAction(final Request request, List<DefaultNoticeRecipient> defaultContacts) {
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

        Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload actionPayload = MAPPER
                .toCca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload(requestPayload, defaultContacts);

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED,
                null);
    }

    private void updateAttachments(List<Cca3FacilityMigrationData> facilityMigrationDataList) throws IOException {
        // Update name and status for existing attachments
        List<FileInfoDTO> attachmentsForUpdate = facilityMigrationDataList.stream()
                .filter(Cca3FacilityMigrationData::isCalculatorFileProvided)
                .map(f -> FileInfoDTO.builder()
                        .uuid(f.getCalculatorFileUuid())
                        .name(f.getCalculatorFileName())
                        .build()
                ).toList();
        ccaFileAttachmentService.updateNameAndStatus(attachmentsForUpdate, FileStatus.SUBMITTED);

        // Create placeholder files
        List<FileInfoDTO> attachmentsForInsert = facilityMigrationDataList.stream()
                .filter(f -> !f.isCalculatorFileProvided() && ObjectUtils.isNotEmpty(f.getCalculatorFileUuid()))
                .map(f -> FileInfoDTO.builder()
                        .uuid(f.getCalculatorFileUuid())
                        .name(f.getCalculatorFileName())
                        .build()
                ).toList();

        if(!attachmentsForInsert.isEmpty()) {
            FileDTO placeholderFile = Cca3ExistingFacilitiesMigrationUtil.getPlaceholderFile();
            ccaFileAttachmentService.createSystemFileAttachments(attachmentsForInsert, placeholderFile, FileStatus.SUBMITTED);
        }
    }

    private void updateFacilities(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload) {
        List<Cca3FacilityMigrationData> facilityMigrations = requestPayload.getFacilityMigrationDataList();
        UnderlyingAgreement underlyingAgreement = requestPayload.getUnderlyingAgreement();

        // Update facilities
        underlyingAgreement.getFacilities().forEach(facility ->
                facilityMigrations.stream()
                        .filter(f -> f.getParticipatingInCca3Scheme() && f.getFacilityBusinessId().equals(facility.getFacilityItem().getFacilityId()))
                        .findFirst().ifPresent(facilityMigration -> {
                            // Update facility
                            Cca3FacilityBaselineAndTargets baselineAndTargets = MAPPER.toCca3FacilityBaselineAndTargets(facilityMigration);
                            facility.getFacilityItem().setCca3BaselineAndTargets(baselineAndTargets);
                            facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().add(SchemeVersion.CCA_3);
                        })
        );

        // Update UNA
        requestPayload.setUnderlyingAgreement(underlyingAgreement);
    }
}
