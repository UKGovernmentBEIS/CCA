package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationFacilityReviewDecisionDataValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationFacilityReviewDecisionDataValidator underlyingAgreementVariationFacilityReviewDecisionDataValidator;

    @Mock
    private DataValidator<UnderlyingAgreementVariationFacilityReviewDecision> dataValidator;

    @Test
    void validateUnderlyingAgreementVariationFacilityReviewDecisions() {
        final String facilityId1 = "facilityId1";
        final UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final UnderlyingAgreementVariationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(
                                                Facility.builder().facilityItem(FacilityItem.builder().facilityId(facilityId1).build()).build(),
                                                Facility.builder().facilityItem(FacilityItem.builder().facilityId("facilityId2").build()).build()
                                        ))
                                        .build())
                                .build())
                        .facilitiesReviewGroupDecisions(Map.of(facilityId1, decision))
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
        final String facilityId1 = "facilityId1";
        final UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final UnderlyingAgreementVariationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .facilityItem(FacilityItem.builder().facilityId(facilityId1).build())
                                                .build()))
                                        .build())
                                .build())
                        .facilitiesReviewGroupDecisions(Map.of(facilityId1, decision))
                        .build();


        when(dataValidator.validate(decision))
                .thenReturn(Optional.of(new BusinessViolation()));

        BusinessValidationResult result = underlyingAgreementVariationFacilityReviewDecisionDataValidator
                .validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(decision);
    }

    @Test
    void validateUnderlyingAgreementVariationFacilityReviewDecisions_not_valid_facility() {
        final UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final UnderlyingAgreementVariationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(
                                                Facility.builder().facilityItem(FacilityItem.builder().facilityId("facilityId1").build()).build(),
                                                Facility.builder().facilityItem(FacilityItem.builder().facilityId("facilityId2").build()).build()
                                        ))
                                        .build())
                                .build())
                        .facilitiesReviewGroupDecisions(Map.of("not_valid", decision))
                        .build();

        when(dataValidator.validate(decision))
                .thenReturn(Optional.empty());

        BusinessValidationResult result = underlyingAgreementVariationFacilityReviewDecisionDataValidator
                .validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(decision);
    }
}
