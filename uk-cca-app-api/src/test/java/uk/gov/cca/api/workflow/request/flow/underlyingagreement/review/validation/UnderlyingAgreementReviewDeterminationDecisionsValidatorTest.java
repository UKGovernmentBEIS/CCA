package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

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
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewDeterminationDecisionsValidatorTest {

    @InjectMocks
    private UnderlyingAgreementReviewDeterminationDecisionsValidator determinationDecisionsValidator;

    @Test
    void isValidAccepted() {
        final Determination determination = Determination.builder()
                .type(DeterminationType.ACCEPTED)
                .build();
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
    void isValidRejectedFacilities() {

        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();

        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.REJECTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()

                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);
        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(UnderlyingAgreementFacilityReviewDecision.class.getName(),
                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                taskPayload.getFacilitiesReviewGroupDecisions().keySet());

        assertThat(result.getViolations().contains(violation)).isFalse();
    }

    @Test
    void isValidRejected() {

        final Determination determination = Determination.builder()
                .type(DeterminationType.REJECTED)
                .build();
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision
                        ))
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(null)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision, "2", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(UnderlyingAgreementFacilityReviewDecision.class.getName(),
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
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.REJECTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision, "2", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision))
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build();

        BusinessValidationResult result = determinationDecisionsValidator
                .validateOverallDecision(taskPayload);

        final ReviewDeterminationViolation violation = new ReviewDeterminationViolation(UnderlyingAgreementFacilityReviewDecision.class.getName(),
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
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                        UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                        UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                        UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                        UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
                                )
                        )
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.REJECTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
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
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
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
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .determination(determination)
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
                        ))
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();


        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision((UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload());

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
        final UnderlyingAgreementFacilityReviewDecision facilitiesDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final UnderlyingAgreementReviewDecision reviewGroupsDecision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(null).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .determination(determination)
                        .facilitiesReviewGroupDecisions(Map.of("1", facilitiesDecision))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
                        ))
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();


        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision((UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload());

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
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .determination(determination)
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD5_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.TARGET_PERIOD6_DETAILS, reviewGroupsDecision,
                                UnderlyingAgreementReviewGroup.AUTHORISATION_AND_ADDITIONAL_EVIDENCE, reviewGroupsDecision
                        ))
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();


        // Invoke
        BusinessValidationResult result = determinationDecisionsValidator.validateOverallDecision((UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload());

        // Verify
        assertThat(result.isValid()).isFalse();
    }

}
