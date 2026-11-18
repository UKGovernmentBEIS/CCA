package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.validation.Cca3ExistingFacilitiesMigrationAccountProcessingValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingService service;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingValidator cca3ExistingFacilitiesMigrationAccountProcessingValidator;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void doProcess() throws BpmnExecutionException, IOException {
        final String requestId = "requestId";

        final String calculatorFileName1 = "attachmentFilename1";
        final String calculatorUuid1 = "33405931-84a5-409e-8721-6f5d5d779a00";
        final String calculatorFileName2 = "attachmentFilename2";
        final String calculatorUuid2 = "1763d416-b2cd-415f-83f8-c0f5d11f4f16";

        Set<SchemeVersion> participatingSchemeVersions = new HashSet<>();
        participatingSchemeVersions.add(SchemeVersion.CCA_2);
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder().cca3Participating(true).build();
        Facility facility1 = Facility.builder()
                .facilityItem(FacilityItem.builder()
                        .facilityDetails(FacilityDetails.builder()
                                .participatingSchemeVersions(participatingSchemeVersions)
                                .build())
                        .facilityId("facility1")
                        .build())
                .build();
        Facility facility2 = Facility.builder()
                .facilityItem(FacilityItem.builder()
                        .facilityDetails(FacilityDetails.builder()
                                .participatingSchemeVersions(participatingSchemeVersions)
                                .build())
                        .facilityId("facility2")
                        .build())
                .build();
        Facility facility3 = Facility.builder()
                .facilityItem(FacilityItem.builder()
                        .facilityDetails(FacilityDetails.builder()
                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                .build())
                        .facilityId("facility3")
                        .build())
                .build();
        final List<Cca3FacilityMigrationData> facilityMigrations = List.of(
                Cca3FacilityMigrationData.builder()
                        .facilityBusinessId("facility1")
                        .participatingInCca3Scheme(true)
                        .baselineDate(LocalDate.of(2025, 1, 1))
                        .explanation("explanation")
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .energyCarbonFactor(BigDecimal.ONE)
                        .usedReportingMechanism(true)
                        .tp7Improvement(BigDecimal.valueOf(10))
                        .tp8Improvement(BigDecimal.valueOf(20))
                        .tp9Improvement(BigDecimal.valueOf(30))
                        .totalFixedEnergy(BigDecimal.valueOf(50))
                        .totalVariableEnergy(BigDecimal.valueOf(60))
                        .totalThroughput(BigDecimal.valueOf(70))
                        .throughputUnit("throughput")
                        .calculatorFileUuid(calculatorUuid1)
                        .calculatorFileName(calculatorFileName1)
                        .calculatorFileProvided(true)
                        .build(),
                Cca3FacilityMigrationData.builder()
                        .facilityBusinessId("facility2")
                        .participatingInCca3Scheme(true)
                        .baselineDate(LocalDate.of(2025, 1, 1))
                        .explanation("explanation")
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .energyCarbonFactor(BigDecimal.ONE)
                        .usedReportingMechanism(true)
                        .tp7Improvement(BigDecimal.valueOf(10))
                        .tp8Improvement(BigDecimal.valueOf(20))
                        .tp9Improvement(BigDecimal.valueOf(30))
                        .totalFixedEnergy(BigDecimal.valueOf(50))
                        .totalVariableEnergy(BigDecimal.valueOf(60))
                        .totalThroughput(BigDecimal.valueOf(70))
                        .throughputUnit("throughput")
                        .calculatorFileUuid(calculatorUuid2)
                        .calculatorFileName(calculatorFileName2)
                        .build(),
                Cca3FacilityMigrationData.builder()
                        .facilityBusinessId("facility3")
                        .participatingInCca3Scheme(false)
                        .build()

        );
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(facility1, facility2, facility3))
                                .build())
                        .facilityMigrationDataList(facilityMigrations)
                        .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().email("test@test.gr").build()
        );

        final Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload actionPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                        .facilityMigrationDataList(facilityMigrations)
                        .defaultContacts(defaultContacts)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request))
                .thenReturn(defaultContacts);

        // Invoke
        service.doProcess(requestId, accountState);

        // Verify
        assertThat(request.getSubmissionDate()).isNull();

        assertThat(facility1.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions()).contains(SchemeVersion.CCA_3);
        assertThat(facility1.getFacilityItem().getCca3BaselineAndTargets()).isEqualTo(createCca3FacilityBaselineAndTargets(calculatorUuid1));

        assertThat(facility2.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions()).contains(SchemeVersion.CCA_3);
        assertThat(facility2.getFacilityItem().getCca3BaselineAndTargets()).isEqualTo(createCca3FacilityBaselineAndTargets(calculatorUuid2));

        assertThat(facility3.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions()).doesNotContain(SchemeVersion.CCA_3);
        assertThat(facility3.getFacilityItem().getCca3BaselineAndTargets()).isNull();

        verify(requestService, times(1)).findRequestById(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingValidator, times(1))
                .validate(accountState, requestPayload);
        verify(ccaFileAttachmentService, times(1))
                .updateNameAndStatus(List.of(FileInfoDTO.builder().name(calculatorFileName1).uuid(calculatorUuid1).build()), FileStatus.SUBMITTED);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachments(eq(List.of(FileInfoDTO.builder().name(calculatorFileName2).uuid(calculatorUuid2).build())), any(), eq(FileStatus.SUBMITTED));
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(requestService, times(1)).addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED,
                null);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService, times(1))
                .generateDocuments(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService, times(1))
                .sendOfficialNotice(requestId);
    }

    @Test
    void doProcess_no_CCA3() throws BpmnExecutionException {
        final String requestId = "requestId";
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder().cca3Participating(false).build();
        final List<Cca3FacilityMigrationData> facilities = List.of(
                Cca3FacilityMigrationData.builder().facilityBusinessId("facility1").participatingInCca3Scheme(false).build(),
                Cca3FacilityMigrationData.builder().facilityBusinessId("facility2").participatingInCca3Scheme(false).build()
        );
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .facilityMigrationDataList(facilities)
                        .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        final Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload actionPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                        .facilityMigrationDataList(facilities)
                        .defaultContacts(List.of())
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.doProcess(requestId, accountState);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(cca3ExistingFacilitiesMigrationAccountProcessingValidator, times(1))
                .validate(accountState, requestPayload);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED,
                null);
        verifyNoInteractions(ccaOfficialNoticeSendService, ccaFileAttachmentService, cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService,
                cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService);
    }

    @Test
    void doProcess_with_error() throws BpmnExecutionException {
        final String requestId = "requestId";
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .cca3Participating(true)
                .errors(List.of("an error"))
                .build();
        final List<Cca3FacilityMigrationData> facilities = List.of(
                Cca3FacilityMigrationData.builder().facilityBusinessId("facility1").participatingInCca3Scheme(true).build(),
                Cca3FacilityMigrationData.builder().facilityBusinessId("facility2").participatingInCca3Scheme(false).build()
        );
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .facilityMigrationDataList(facilities)
                        .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.doProcess(requestId, accountState);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingValidator, times(1))
                .validate(accountState, requestPayload);
        verifyNoInteractions(ccaOfficialNoticeSendService, ccaFileAttachmentService, cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService,
                cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService);
        verifyNoMoreInteractions(requestService);
    }

    private Cca3FacilityBaselineAndTargets createCca3FacilityBaselineAndTargets(String calculatorUuid) {
        return Cca3FacilityBaselineAndTargets.builder()
                .targetComposition(FacilityTargetComposition.builder()
                        .calculatorFile(UUID.fromString(calculatorUuid))
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                        .build())
                .baselineData(FacilityBaselineData.builder()
                        .isTwelveMonths(true)
                        .baselineDate(LocalDate.of(2025, 1, 1))
                        .explanation("explanation")
                        .usedReportingMechanism(true)
                        .energyCarbonFactor(BigDecimal.ONE)
                        .build())
                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                        .totalFixedEnergy(BigDecimal.valueOf(50))
                        .hasVariableEnergy(true)
                        .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                        .baselineVariableEnergy(BigDecimal.valueOf(60))
                        .totalThroughput(BigDecimal.valueOf(70))
                        .throughputUnit("throughput")
                        .build())
                .facilityTargets(FacilityTargets.builder()
                        .improvements(Map.of(
                                TargetImprovementType.TP7, BigDecimal.valueOf(10),
                                TargetImprovementType.TP8, BigDecimal.valueOf(20),
                                TargetImprovementType.TP9, BigDecimal.valueOf(30)
                        ))
                        .build())
                .build();
    }
}
