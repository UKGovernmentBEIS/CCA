package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewDecisionDataValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;

    @Mock
    private DataValidator<UnderlyingAgreementReviewDecision> dataValidator;

    @Test
    void validateUnderlyingAgreementVariationReviewDecisions() {
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .build();

        when(dataValidator.validate(decision))
                .thenReturn(Optional.empty());

        BusinessValidationResult result = underlyingAgreementVariationReviewDecisionDataValidator
                .validateUnderlyingAgreementVariationReviewDecisions(taskPayload);

        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(1)).validate(decision);
    }

    @Test
    void validateUnderlyingAgreementVariationReviewDecisions_not_valid() {
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .build();

        when(dataValidator.validate(decision))
                .thenReturn(Optional.of(new BusinessViolation()));

        BusinessValidationResult result = underlyingAgreementVariationReviewDecisionDataValidator
                .validateUnderlyingAgreementVariationReviewDecisions(taskPayload);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(decision);
    }
}
