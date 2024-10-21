package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewDecisionDataValidatorTest {

    @InjectMocks
    private UnderlyingAgreementReviewDecisionDataValidator underlyingAgreementReviewDecisionDataValidator;

    @Mock
    private DataValidator<UnderlyingAgreementReviewDecision> dataValidator;

    @Test
    void validateUnderlyingAgreementReviewDecisions() {
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().build())
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .build();

        when(dataValidator.validate(decision))
                .thenReturn(Optional.empty());

        BusinessValidationResult result = underlyingAgreementReviewDecisionDataValidator
                .validateUnderlyingAgreementReviewDecisions(taskPayload);

        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(1)).validate(decision);
    }

    @Test
    void validateUnderlyingAgreementReviewDecisions_not_valid() {
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().build())
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .build();

        when(dataValidator.validate(decision))
                .thenReturn(Optional.of(new BusinessViolation()));

        BusinessValidationResult result = underlyingAgreementReviewDecisionDataValidator
                .validateUnderlyingAgreementReviewDecisions(taskPayload);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(decision);
    }
}
