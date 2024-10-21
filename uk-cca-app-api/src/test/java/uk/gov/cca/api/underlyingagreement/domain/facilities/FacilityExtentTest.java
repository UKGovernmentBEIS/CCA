package uk.gov.cca.api.underlyingagreement.domain.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FacilityExtentTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_all_fields_exist_valid() {
    	FacilityExtent facilityExtent = FacilityExtent.builder()
    			.manufacturingProcessFile(UUID.randomUUID())
    			.processFlowFile(UUID.randomUUID())
    			.annotatedSitePlansFile(UUID.randomUUID())
    			.eligibleProcessFile(UUID.randomUUID())
    			.areActivitiesClaimed(Boolean.TRUE)
    			.activitiesDescriptionFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<FacilityExtent>> violations = validator.validate(facilityExtent);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_activitiesDescriptionFile_not_exist_invalid() {
    	FacilityExtent facilityExtent = FacilityExtent.builder()
    			.manufacturingProcessFile(UUID.randomUUID())
    			.processFlowFile(UUID.randomUUID())
    			.annotatedSitePlansFile(UUID.randomUUID())
    			.eligibleProcessFile(UUID.randomUUID())
    			.areActivitiesClaimed(Boolean.TRUE)
    			.build();

        final Set<ConstraintViolation<FacilityExtent>> violations = validator.validate(facilityExtent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{underlyingagreement.facilities.facilityextent.areActivitiesClaimed}");
    }
}
