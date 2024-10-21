package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationFacilityReviewDecisionDataValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationFacilityReviewDecisionDataValidator underlyingAgreementVariationFacilityReviewDecisionDataValidator;

    @Mock
    private DataValidator<UnderlyingAgreementVariationFacilityReviewDecision> dataValidator;

    @Test
    void validateUnderlyingAgreementVariationFacilityReviewDecisions() {
        final UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().build())
                        .facilitiesReviewGroupDecisions(Map.of("1", decision))
                        .build();

        when(dataValidator.validate(decision))
                .thenReturn(Optional.empty());

        BusinessValidationResult result = underlyingAgreementVariationFacilityReviewDecisionDataValidator
                .validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload);

        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(1)).validate(decision);
    }

    @Test
    void validateUnderlyingAgreementVariationFacilityReviewDecisions_not_valid() {
        final UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().build())
                        .facilitiesReviewGroupDecisions(Map.of("1", decision))
                        .build();


        when(dataValidator.validate(decision))
                .thenReturn(Optional.of(new BusinessViolation()));

        BusinessValidationResult result = underlyingAgreementVariationFacilityReviewDecisionDataValidator
                .validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(decision);
    }
}
