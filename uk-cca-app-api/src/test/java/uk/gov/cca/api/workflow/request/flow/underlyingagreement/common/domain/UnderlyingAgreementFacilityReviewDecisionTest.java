package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;

class UnderlyingAgreementFacilityReviewDecisionTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_all_fields_exist_valid() {
    	UnderlyingAgreementFacilityReviewDecision decision = UnderlyingAgreementFacilityReviewDecision.builder()
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.changeStartDate(Boolean.TRUE)
    			.startDate(LocalDate.now())
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_changeStartDate_not_exist_invalid() {
    	UnderlyingAgreementFacilityReviewDecision decision = UnderlyingAgreementFacilityReviewDecision.builder()
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.review.changeStartDate}");
    }
    
    @Test
    void validate_startDate_not_exist_invalid() {
    	UnderlyingAgreementFacilityReviewDecision decision = UnderlyingAgreementFacilityReviewDecision.builder()
    			.details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
    			.type(CcaReviewDecisionType.ACCEPTED)
    			.changeStartDate(Boolean.TRUE)
    			.build();

        final Set<ConstraintViolation<UnderlyingAgreementFacilityReviewDecision>> violations = 
        		validator.validate(decision);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.review.startDate}");
    }
}
