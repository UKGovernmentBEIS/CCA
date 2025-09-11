package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecisionDetails;

class UnderlyingAgreementVariationFacilityReviewDecisionTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_accepted_live_valid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.facilityStatus(FacilityStatus.LIVE)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations =
        		validator.validate(decision);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_accepted_new_valid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.changeStartDate(Boolean.TRUE)
    			.startDate(LocalDate.now())
    			.facilityStatus(FacilityStatus.NEW)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_rejected_valid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.type(CcaReviewDecisionType.REJECTED)
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.facilityStatus(FacilityStatus.EXCLUDED)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_accepted_new_invalid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.facilityStatus(FacilityStatus.NEW)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.variation.review.changeStartDate}");
    }
    
    @Test
    void validate_accepted_live_invalid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.changeStartDate(Boolean.FALSE)
    			.facilityStatus(FacilityStatus.LIVE)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.variation.review.changeStartDate}");
    }
    
    @Test
    void validate_accepted_new_startDate_not_exist_invalid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.changeStartDate(Boolean.TRUE)
    			.facilityStatus(FacilityStatus.NEW)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.review.startDate}");
    }
    
    @Test
    void validate_rejected_invalid() {
    	UnderlyingAgreementVariationFacilityReviewDecision decision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.type(CcaReviewDecisionType.REJECTED)
    			.changeStartDate(Boolean.FALSE)
    			.facilityStatus(FacilityStatus.LIVE)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementVariationFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.variation.review.changeStartDate}");
    }
}
