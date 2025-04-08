package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityDetailsTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_all_fields_exist_valid() {
    	FacilityDetails facilityDetails = FacilityDetails.builder()
                .name("Facility name")
    			.isCoveredByUkets(Boolean.TRUE)
    			.uketsId("uketsId")
    			.applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
    			.previousFacilityId("AAA_1-F11111")
    			.facilityAddress(AccountAddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code")
                        .country("Country")
                        .build())
    			.build();

        final Set<ConstraintViolation<FacilityDetails>> violations = validator.validate(facilityDetails);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_uketsId_previousFacilityId_not_exist_invalid() {
    	FacilityDetails facilityDetails = FacilityDetails.builder()
                .name("Facility name")
    			.isCoveredByUkets(Boolean.TRUE)
    			.applicationReason(ApplicationReasonType.NEW_AGREEMENT)
    			.previousFacilityId("previousFacilityId")
    			.facilityAddress(AccountAddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code")
                        .country("Country")
                        .build())
    			.build();

        final Set<ConstraintViolation<FacilityDetails>> violations = validator.validate(facilityDetails);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                		"{underlyingagreement.facilities.facilitydetails.isCoveredByUkets}",
                		"{underlyingagreement.facilities.facilitydetails.applicationReason}",
                        "underlyingagreement.facilities.facilitydetails.previousFacilityId"
                        );
    }
}
