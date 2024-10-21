package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

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
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewSavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewRequestTaskActionPayload;
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
        final TargetPeriod5Details targetPeriod5Details = TargetPeriod5Details.builder()
                .exist(false)
                .build();
        final TargetPeriod6Details targetPeriod6Details = TargetPeriod6Details.builder()
                .targetComposition(TargetComposition.builder().measurementType(MeasurementType.ENERGY_GJ).build())
                .build();
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
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(reviewRequestTaskPayload).build();


        UnderlyingAgreementSaveReviewRequestTaskActionPayload reviewRequestTaskActionPayload =
                UnderlyingAgreementSaveReviewRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementReviewSavePayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .facilities(facilityItems)
                                .targetPeriod5Details(targetPeriod5Details)
                                .targetPeriod6Details(targetPeriod6Details)
                                .authorisationAndAdditionalEvidence(evidence)
                                .build())
                        .sectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
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
                .isEqualTo(targetPeriod5Details);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod6Details())
                .isEqualTo(targetPeriod6Details);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence())
                .isEqualTo(evidence);
        assertThat(payloadSaved.getSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(reviewRequestTaskActionPayload.getSectionsCompleted());
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(reviewRequestTaskActionPayload.getReviewSectionsCompleted());
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

        service.saveReviewGroupDecision(payload, requestTask);

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

        service.saveFacilityReviewGroupDecision(payload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);

        final UnderlyingAgreementReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getFacilitiesReviewGroupDecisions()).containsEntry("ADS-F00064", decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
        assertThat(taskPayload.getDetermination()).isNull();
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
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, UnderlyingAgreementReviewDecision.builder().type(CcaReviewDecisionType.REJECTED).build()
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
}
