package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementApplySavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewServiceTest {

    @InjectMocks
    private UnderlyingAgreementReviewService service;

    @Test
    void saveUnderlyingAgreement() {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder().applicationReason(ApplicationReasonType.NEW_AGREEMENT).build())
                .build();
        final Set<FacilityItem> facilityItems = Set.of(facilityItem1);
        final AuthorisationAndAdditionalEvidence evidence = AuthorisationAndAdditionalEvidence.builder()
                .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                .build();

        UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementReviewRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().build())
                                .underlyingAgreement(UnderlyingAgreement.builder().build())
                                .build())
                        .determination(Determination.builder()
                                .type(DeterminationType.ACCEPTED)
                                .additionalInformation("info")
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(reviewRequestTaskPayload).build();


        UnderlyingAgreementSaveReviewRequestTaskActionPayload reviewRequestTaskActionPayload =
                UnderlyingAgreementSaveReviewRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementApplySavePayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .facilities(facilityItems)
                                .authorisationAndAdditionalEvidence(evidence)
                                .build())
                        .sectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().type(DeterminationType.ACCEPTED).reason("Reason etc").additionalInformation("info").build())
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();

        final Set<Facility> facilities = Set.of(
                Facility.builder()
                        .status(FacilityStatus.NEW)
                        .facilityItem(facilityItem1)
                        .build());

        // Invoke
        service.saveUnderlyingAgreement(reviewRequestTaskActionPayload, requestTask);

        //verify
        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);

        UnderlyingAgreementReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails())
                .isEqualTo(reviewRequestTaskActionPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails());
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities())
                .isEqualTo(facilities);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod5Details())
                .isNull();
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod6Details())
                .isNull();
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence())
                .isEqualTo(evidence);
        assertThat(payloadSaved.getSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(reviewRequestTaskActionPayload.getSectionsCompleted());
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(reviewRequestTaskActionPayload.getReviewSectionsCompleted());
        assertThat(payloadSaved.getDetermination().getAdditionalInformation()).isEqualTo("info");
    }

    @Test
    void saveReviewGroupDecision() {
        final UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(reviewRequestTaskPayload)
                .request(Request.builder().build())
                .build();

        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .type(CcaReviewDecisionType.REJECTED)
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();

        final UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload payload =
                UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                        .group(UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS)
                        .decision(UnderlyingAgreementReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED).details(UnderlyingAgreementReviewDecisionDetails.builder()
                                        .notes("notes")
                                        .build())
                                .build())
                        .build();

        // Invoke
        service.saveReviewGroupDecision(payload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);

        final UnderlyingAgreementReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getReviewGroupDecisions()).containsEntry(UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
    }

    @Test
    void saveFacilityReviewGroupDecision_resetDetermination() {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().type(DeterminationType.ACCEPTED).build())
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().build())
                .build();

        final UnderlyingAgreementFacilityReviewDecision decision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();

        final UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload payload =
                UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD)
                        .group("ADS-F00064")
                        .decision(UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .changeStartDate(Boolean.TRUE)
                                .startDate(LocalDate.now())
                                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                                        .notes("notes")
                                        .build())
                                .build())
                        .build();

        // Invoke
        service.saveFacilityReviewGroupDecision(payload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);

        final UnderlyingAgreementReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getFacilitiesReviewGroupDecisions()).containsEntry("ADS-F00064", decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
    }

    @Test
    void saveDetermination() {
        UnderlyingAgreementPayload una = UnderlyingAgreementPayload.builder()
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .build();

        UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .underlyingAgreement(una)
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, UnderlyingAgreementReviewDecision.builder().type(CcaReviewDecisionType.ACCEPTED).build(),
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, UnderlyingAgreementReviewDecision.builder().type(CcaReviewDecisionType.REJECTED).build()
                        ))
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(reviewRequestTaskPayload).build();

        UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload reviewSaveDeterminationPayload =
                UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                        .determination(Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build())
                        .build();

        service.saveDetermination(reviewSaveDeterminationPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);

        UnderlyingAgreementReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getDetermination().getReason())
                .isEqualTo(reviewSaveDeterminationPayload.getDetermination().getReason());
        assertThat(payloadSaved.getDetermination().getType())
                .isEqualTo(reviewSaveDeterminationPayload.getDetermination().getType());
        assertThat(payloadSaved.getDetermination().getAdditionalInformation())
                .isEqualTo(reviewSaveDeterminationPayload.getDetermination().getAdditionalInformation());
        assertThat(payloadSaved.getDetermination().getFiles())
                .isEqualTo(reviewSaveDeterminationPayload.getDetermination().getFiles());
    }

    @Test
    void notifyOperator() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();
        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(reviewRequestTaskPayload)
                .request(Request.builder().payload(requestPayload).build())
                .build();

        service.notifyOperator(requestTask, decisionNotification, appUser);

        UnderlyingAgreementRequestPayload actual = (UnderlyingAgreementRequestPayload) requestTask.getRequest().getPayload();
        assertThat(actual.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
        assertThat(actual.getSectionsCompleted()).isEqualTo(reviewRequestTaskPayload.getSectionsCompleted());
        assertThat(actual.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(actual.getUnderlyingAgreement()).isEqualTo(reviewRequestTaskPayload.getUnderlyingAgreement());
        assertThat(actual.getUnderlyingAgreementAttachments()).isEqualTo(reviewRequestTaskPayload.getUnderlyingAgreementAttachments());
        assertThat(actual.getReviewSectionsCompleted()).isEqualTo(reviewRequestTaskPayload.getReviewSectionsCompleted());
        assertThat(actual.getReviewGroupDecisions()).isEqualTo(reviewRequestTaskPayload.getReviewGroupDecisions());
        assertThat(actual.getFacilitiesReviewGroupDecisions()).isEqualTo(reviewRequestTaskPayload.getFacilitiesReviewGroupDecisions());
        assertThat(actual.getReviewAttachments()).isEqualTo(reviewRequestTaskPayload.getReviewAttachments());
        assertThat(actual.getDetermination()).isEqualTo(reviewRequestTaskPayload.getDetermination());
    }

    @Test
    void requestPeerReview() {
        final String selectedPeerReviewer = UUID.randomUUID().toString();
        final String regulatorReviewer = UUID.randomUUID().toString();
        final AppUser appUser = AppUser.builder().userId(regulatorReviewer).build();
        final UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();
        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(reviewRequestTaskPayload)
                .request(Request.builder().payload(requestPayload).build())
                .build();

        service.requestPeerReview(requestTask, selectedPeerReviewer, regulatorReviewer);

        UnderlyingAgreementRequestPayload actual = (UnderlyingAgreementRequestPayload) requestTask.getRequest().getPayload();
        assertThat(actual.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
        assertThat(actual.getRegulatorPeerReviewer()).isEqualTo(selectedPeerReviewer);
        assertThat(actual.getSectionsCompleted()).isEqualTo(reviewRequestTaskPayload.getSectionsCompleted());
        assertThat(actual.getUnderlyingAgreement()).isEqualTo(reviewRequestTaskPayload.getUnderlyingAgreement());
        assertThat(actual.getUnderlyingAgreementAttachments()).isEqualTo(reviewRequestTaskPayload.getUnderlyingAgreementAttachments());
        assertThat(actual.getReviewSectionsCompleted()).isEqualTo(reviewRequestTaskPayload.getReviewSectionsCompleted());
        assertThat(actual.getReviewGroupDecisions()).isEqualTo(reviewRequestTaskPayload.getReviewGroupDecisions());
        assertThat(actual.getFacilitiesReviewGroupDecisions()).isEqualTo(reviewRequestTaskPayload.getFacilitiesReviewGroupDecisions());
        assertThat(actual.getReviewAttachments()).isEqualTo(reviewRequestTaskPayload.getReviewAttachments());
        assertThat(actual.getDetermination()).isEqualTo(reviewRequestTaskPayload.getDetermination());
    }
}
