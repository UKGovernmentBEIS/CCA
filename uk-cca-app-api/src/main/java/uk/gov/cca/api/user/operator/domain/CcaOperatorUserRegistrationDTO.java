package uk.gov.cca.api.user.operator.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.validation.PhoneNumberIntegrity;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class CcaOperatorUserRegistrationDTO {

    @NotBlank(message = "{jwt.token.notEmpty}")
    private String emailToken;

    @NotBlank(message = "{userAccount.firstName.notEmpty}")
    @Size(max = 255, message = "{userAccount.firstName.typeMismatch}")
    private String firstName;

    @NotBlank(message = "{userAccount.lastName.notEmpty}")
    @Size(max = 255, message = "{userAccount.lastName.typeMismatch}")
    private String lastName;

    @Size(max = 255, message = "{userAccount.jobTitle.typeMismatch}")
    private String jobTitle;

    @PhoneNumberIntegrity(message = "{userAccount.phoneNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO phoneNumber;

    @PhoneNumberIntegrity(message = "{userAccount.mobileNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO mobileNumber;
    
    @NotNull
    private ContactType contactType;

    private String organisationName;
}