package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType.AMEND_OPERATOR_OR_ORGANISATION_NAME;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitService service;

    @Test
    void saveUnderlyingAgreementVariation() {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1")
                        .facilityDetails(FacilityDetails.builder().applicationReason(ApplicationReasonType.NEW_AGREEMENT).build()).build())
                .build());
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("1", LocalDate.now());
        final TargetPeriod5Details targetPeriod5Details = TargetPeriod5Details.builder()
                .exist(false)
                .build();
        final TargetPeriod6Details targetPeriod6Details = TargetPeriod6Details.builder()
                .targetComposition(TargetComposition.builder().measurementType(MeasurementType.ENERGY_GJ).build())
                .build();
        final AuthorisationAndAdditionalEvidence evidence = AuthorisationAndAdditionalEvidence.builder()
                .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                .build();
        final UnderlyingAgreementVariationDetails variationDetails = UnderlyingAgreementVariationDetails.builder()
                .reason("reason").modifications(Collections.singletonList(AMEND_OPERATOR_OR_ORGANISATION_NAME)).build();

        UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().build())
                                .underlyingAgreement(UnderlyingAgreement.builder().build())
                                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder().build())
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();


        UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload taskActionPayload =
                UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationRegulatorLedSavePayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .facilities(facilities)
                                .facilityChargeStartDateMap(facilityChargeStartDateMap)
                                .targetPeriod5Details(targetPeriod5Details)
                                .targetPeriod6Details(targetPeriod6Details)
                                .authorisationAndAdditionalEvidence(evidence)
                                .underlyingAgreementVariationDetails(variationDetails)
                                .build())
                        .sectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();

        final Set<Facility> facilitiesSet = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1")
                        .facilityDetails(FacilityDetails.builder().applicationReason(ApplicationReasonType.NEW_AGREEMENT).build()).build())
                .build());

        // Invoke
        service.saveUnderlyingAgreementVariation(taskActionPayload, requestTask);

        // Verify
        assertThat(taskPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails())
                .isEqualTo(taskActionPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails());
        assertThat(taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities())
                .isEqualTo(facilitiesSet);
        assertThat(taskPayload.getFacilityChargeStartDateMap())
                .containsExactlyInAnyOrderEntriesOf(facilityChargeStartDateMap);
        assertThat(taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod5Details())
                .isEqualTo(targetPeriod5Details);
        assertThat(taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod6Details())
                .isEqualTo(targetPeriod6Details);
        assertThat(taskPayload.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails())
                .isEqualTo(variationDetails);
        assertThat(taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence())
                .isEqualTo(evidence);
        assertThat(taskPayload.getSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(taskActionPayload.getSectionsCompleted());
    }

    @Test
    void notifyOperator() {
        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();

        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder().build())
                .build();
        final UnderlyingAgreementVariationPayload underlyingAgreement = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).build()))
                        .build())
                .build();
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("facility", LocalDate.now());
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Map<UUID, String> underlyingAgreementAttachments = Map.of(UUID.randomUUID(), "attachment");
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "reg-led-attachment");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "COMPLETED");
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreement)
                        .facilityChargeStartDateMap(facilityChargeStartDateMap)
                        .determination(determination)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        // Invoke
        service.notifyOperator(requestTask, decisionNotification);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getUnderlyingAgreementProposed()).isEqualTo(underlyingAgreement);
        assertThat(requestPayload.getUnderlyingAgreementAttachments()).isEqualTo(underlyingAgreementAttachments);
        assertThat(requestPayload.getRegulatorLedSubmitAttachments()).isEqualTo(regulatorLedSubmitAttachments);
        assertThat(requestPayload.getRegulatorLedDetermination()).isEqualTo(determination);
        assertThat(requestPayload.getAccountReferenceData()).isEqualTo(accountReferenceData);
        assertThat(requestPayload.getRegulatorLedFacilityChargeStartDateMap()).isEqualTo(facilityChargeStartDateMap);
    }

    @Test
    void requestPeerReview() {
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final String regulatorReviewer = "regulatorReviewer";
        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();

        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder().build())
                .build();
        final UnderlyingAgreementVariationPayload underlyingAgreement = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).build()))
                        .build())
                .build();
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("facility", LocalDate.now());
        final Map<UUID, String> underlyingAgreementAttachments = Map.of(UUID.randomUUID(), "attachment");
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "reg-led-attachment");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "COMPLETED");
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreement)
                        .facilityChargeStartDateMap(facilityChargeStartDateMap)
                        .determination(determination)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        // Invoke
        service.requestPeerReview(requestTask, selectedPeerReviewer, regulatorReviewer);

        // Verify
        assertThat(request.getSubmissionDate()).isNull();
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getUnderlyingAgreementProposed()).isEqualTo(underlyingAgreement);
        assertThat(requestPayload.getUnderlyingAgreementAttachments()).isEqualTo(underlyingAgreementAttachments);
        assertThat(requestPayload.getRegulatorLedSubmitAttachments()).isEqualTo(regulatorLedSubmitAttachments);
        assertThat(requestPayload.getRegulatorLedDetermination()).isEqualTo(determination);
        assertThat(requestPayload.getAccountReferenceData()).isEqualTo(accountReferenceData);
        assertThat(requestPayload.getRegulatorLedFacilityChargeStartDateMap()).isEqualTo(facilityChargeStartDateMap);
        assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(selectedPeerReviewer);
        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(regulatorReviewer);
    }
}
