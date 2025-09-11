package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

class TargetUnitAccountPayloadTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_all_fields_valid() {
    	TargetUnitAccountPayload payload = TargetUnitAccountPayload.builder()
    			.name("account")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .isCompanyRegistrationNumber(Boolean.TRUE)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .address(AccountAddressDTO.builder()
                		.city("city")
                		.line1("line1")
                		.line2("line2")
                		.country("country")
                		.postcode("postcode")
                		.build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .subsectorAssociationId(1L)
                .subsectorAssociationName("subsector")
                .build();

        final Set<ConstraintViolation<TargetUnitAccountPayload>> violations = validator.validate(payload);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_subsectorName_not_exist_invalid() {
    	TargetUnitAccountPayload payload = TargetUnitAccountPayload.builder()
    			.name("account")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .isCompanyRegistrationNumber(Boolean.TRUE)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .address(AccountAddressDTO.builder()
                		.city("city")
                		.line1("line1")
                		.line2("line2")
                		.country("country")
                		.postcode("postcode")
                		.build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .subsectorAssociationId(1L)
                .build();

        final Set<ConstraintViolation<TargetUnitAccountPayload>> violations = validator.validate(payload);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly(
                		"{target.unit.account.subsectorAssociationName.and.subsectorAssociationId.inconsistency}"
                        );
    }
    
    private TargetUnitAccountContactDTO getAdministrativeContactDetailsDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle")
                .firstName("firstName")
                .lastName("lastName")
                .address(AccountAddressDTO.builder()
                		.city("city")
                		.line1("line1")
                		.line2("line2")
                		.country("country")
                		.postcode("postcode")
                		.build())
                .email("email@email.com")
                .build();
    }

    private TargetUnitAccountContactDTO getResponsiblePersonDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle")
                .firstName("firstName")
                .lastName("lastName")
                .address(AccountAddressDTO.builder()
                		.city("city")
                		.line1("line1")
                		.line2("line2")
                		.country("country")
                		.postcode("postcode")
                		.build())
                .email("email@email.com")
                .build();
    }
}
