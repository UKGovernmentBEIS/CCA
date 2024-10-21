package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service;

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
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewSavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType.AMEND_OPERATOR_OR_ORGANISATION_NAME;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewService service;


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

        UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().build())
                                .underlyingAgreement(UnderlyingAgreement.builder().build())
                                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder().build())
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(reviewRequestTaskPayload).build();


        UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload reviewRequestTaskActionPayload =
                UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementVariationReviewSavePayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .facilities(facilities)
                                .targetPeriod5Details(targetPeriod5Details)
                                .targetPeriod6Details(targetPeriod6Details)
                                .authorisationAndAdditionalEvidence(evidence)
                                .underlyingAgreementVariationDetails(variationDetails)
                                .build())
                        .sectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();

        final Set<Facility> facilitiesSet = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1")
                        .facilityDetails(FacilityDetails.builder().applicationReason(ApplicationReasonType.NEW_AGREEMENT).build()).build())
                .build());

        // Invoke
        service.saveUnderlyingAgreementVariation(reviewRequestTaskActionPayload, requestTask);

        //verify
        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementVariationReviewRequestTaskPayload.class);

        UnderlyingAgreementVariationReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails())
                .isEqualTo(reviewRequestTaskActionPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails());
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities())
                .isEqualTo(facilitiesSet);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod5Details())
                .isEqualTo(targetPeriod5Details);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getTargetPeriod6Details())
                .isEqualTo(targetPeriod6Details);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails())
                .isEqualTo(variationDetails);
        assertThat(payloadSaved.getUnderlyingAgreement().getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence())
                .isEqualTo(evidence);
        assertThat(payloadSaved.getSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(reviewRequestTaskActionPayload.getSectionsCompleted());
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(reviewRequestTaskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void saveReviewGroupDecision() {
        final UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
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

        final UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload payload =
                UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                        .group(UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS)
                        .decision(UnderlyingAgreementReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED).details(UnderlyingAgreementReviewDecisionDetails.builder()
                                        .notes("notes")
                                        .build())
                                .build())
                        .build();

        service.saveReviewGroupDecision(payload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementVariationReviewRequestTaskPayload.class);

        final UnderlyingAgreementVariationReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getReviewGroupDecisions()).containsEntry(UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
    }

    @Test
    void saveFacilityReviewGroupDecision() {
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().build())
                .build();

        final UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();

        final UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload payload =
                UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD)
                        .group("ADS-F00064")
                        .decision(UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .changeStartDate(Boolean.TRUE)
                                .startDate(LocalDate.now())
                                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                                        .notes("notes")
                                        .build())
                                .build())
                        .build();


        service.saveFacilityReviewGroupDecision(payload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementVariationReviewRequestTaskPayload.class);

        final UnderlyingAgreementVariationReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getFacilitiesReviewGroupDecisions()).containsEntry("ADS-F00064", decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
                .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
    }

    @Test
    void saveDetermination() {
        UnderlyingAgreementVariationPayload una = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .build();

        UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .underlyingAgreement(una)
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, UnderlyingAgreementReviewDecision.builder().type(CcaReviewDecisionType.ACCEPTED).build(),
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, UnderlyingAgreementReviewDecision.builder().type(CcaReviewDecisionType.REJECTED).build()
                        ))
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(reviewRequestTaskPayload).build();

        UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload reviewSaveDeterminationPayload =
                UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                        .determination(Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build())
                        .build();

        service.saveDetermination(reviewSaveDeterminationPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(UnderlyingAgreementVariationReviewRequestTaskPayload.class);

        UnderlyingAgreementVariationReviewRequestTaskPayload
                payloadSaved = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

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
        final UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(reviewRequestTaskPayload)
                .request(Request.builder().payload(requestPayload).build())
                .build();

        service.notifyOperator(requestTask, decisionNotification, appUser);

        UnderlyingAgreementVariationRequestPayload actual = (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();
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
}
