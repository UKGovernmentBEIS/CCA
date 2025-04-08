package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EligibilityDetailsAndAuthorisationTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_all_fields_exist_valid() {
    	EligibilityDetailsAndAuthorisation details = EligibilityDetailsAndAuthorisation.builder()
    			.isConnectedToExistingFacility(Boolean.TRUE)
    			.adjacentFacilityId("AAA_1-F11111")
    			.agreementType(AgreementType.ENVIRONMENTAL_PERMITTING_REGULATIONS)
    			.erpAuthorisationExists(Boolean.TRUE)
    			.authorisationNumber("authorisationNumber")
    			.regulatorName(RegulatorNameType.ENVIRONMENT_AGENCY)
    			.permitFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<EligibilityDetailsAndAuthorisation>> violations = validator.validate(details);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_fields_not_exist_invalid() {
    	EligibilityDetailsAndAuthorisation details = EligibilityDetailsAndAuthorisation.builder()
    			.isConnectedToExistingFacility(Boolean.TRUE)
    			.agreementType(AgreementType.ENVIRONMENTAL_PERMITTING_REGULATIONS)
    			.erpAuthorisationExists(Boolean.FALSE)
    			.authorisationNumber("authorisationNumber")
    			.regulatorName(RegulatorNameType.ENVIRONMENT_AGENCY)
    			.permitFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<EligibilityDetailsAndAuthorisation>> violations = validator.validate(details);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                		"{underlyingagreement.facilities.authorisation.isConnectedToExistingFacility}",
                		"{underlyingagreement.facilities.authorisation.authorisationNumber}",
                		"{underlyingagreement.facilities.authorisation.regulatorName}",
                		"{underlyingagreement.facilities.authorisation.permitFile}");
    }

	@Test
    void validate_adjacentFacilityId_invalid() {
    	EligibilityDetailsAndAuthorisation details = EligibilityDetailsAndAuthorisation.builder()
    			.isConnectedToExistingFacility(Boolean.TRUE)
    			.agreementType(AgreementType.ENVIRONMENTAL_PERMITTING_REGULATIONS)
    			.erpAuthorisationExists(Boolean.FALSE)
				.adjacentFacilityId("incorrect adjacent facility id")
				.authorisationNumber("authorisationNumber")
    			.regulatorName(RegulatorNameType.ENVIRONMENT_AGENCY)
    			.permitFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<EligibilityDetailsAndAuthorisation>> violations = validator.validate(details);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("underlyingagreement.facilities.authorisation.adjacentFacilityId");
    }
}
