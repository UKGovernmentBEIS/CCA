package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationReviewDeterminationDecisionsValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewDeterminationDecisionsValidator determinationDecisionsValidator;

    @Test
    void isValidAccepted() {
        final Determination determination = Determination.builder()
                .type(DeterminationType.ACCEPTED)
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .facilityStatus(FacilityStatus.NEW)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(
                Determination.class.getName(),
                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA);
        assertThat(result.getViolations().contains(violation)).isFalse();

    }

    @Test
    void isValidAccepted_no_active_facilities() {
        final Determination determination = Determination.builder()
                .type(DeterminationType.ACCEPTED)
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision_1 = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .facilityStatus(FacilityStatus.EXCLUDED)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision_2 = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.REJECTED)
                .changeStartDate(Boolean.TRUE)
                .facilityStatus(FacilityStatus.NEW)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision_1, "2", facilitiesDecision_2))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(
                Determination.class.getName(),
                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA);

        assertThat(result.getViolations().contains(violation)).isTrue();

    }

    @Test
    void isValidRejectedFacilities() {

        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();

        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.REJECTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()

                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);
        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                taskPayload.getFacilitiesReviewGroupDecisions().keySet());

        assertThat(result.getViolations().contains(violation)).isFalse();
    }

    @Test
    void isValidRejected() {

        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS, reviewGroupsDecision

                        ))
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void isValid_isFacilitiesReviewGroupDecisionsInvalid_true() {

        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(null)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision, "2", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                taskPayload.getFacilitiesReviewGroupDecisions().keySet());

        assertThat(result.getViolations().contains(violation)).isTrue();

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void isValid_isFacilitiesReviewGroupDecisionsValid_false() {
        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.REJECTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision, "2", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                taskPayload.getFacilitiesReviewGroupDecisions().keySet());

        assertThat(result.getViolations().contains(violation)).isTrue();

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void isValid_getReviewGroupDecisions_REJECTED() {
        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                        UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                        UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                        UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                        UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision,
                                        UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS, reviewGroupsDecision
                                )
                        )
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        assertThat(result.getViolations()).isEmpty();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateOverallDecision_rejected() {
        final Determination determination = Determination.builder().type(DeterminationType.REJECTED).additionalInformation("text").build();
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of(
                                "1", facilitiesDecision)
                        )
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS, reviewGroupsDecision
                        ))
                        .build();

        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();

    }

    @Test
    void validateOverallDecision_Accepted() {
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .facilityStatus(FacilityStatus.NEW)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of(
                                "1", facilitiesDecision)
                        )
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS, reviewGroupsDecision
                        ))
                        .build();

        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
    }


    @Test
    void validateOverallDecision_accepted_not_valid() {
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();

        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .determination(determination)
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS, reviewGroupsDecision
                        ))
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();


        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision((UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload());

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);

    }

    @Test
    void validateOverallDecision_rejected_not_valid() {
        final Determination determination = Determination.builder().type(DeterminationType.REJECTED).additionalInformation("text").build();
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementVariationFacilityReviewDecision facilitiesDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(null).build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
                        ))
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();


        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision((UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload());

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);

    }

    @Test
    void validateOverallDecision_accepted_not_valid_emptyReviewDecision() {
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();

        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(null).build();
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .determination(determination)
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementVariationReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
                        ))
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();


        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision((UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload());

        // Verify
        assertThat(result.isValid()).isFalse();
    }
}
