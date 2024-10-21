package uk.gov.cca.api.user.operator.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.validation.PhoneNumberIntegrity;
import uk.gov.netz.api.user.core.domain.dto.UserDTO;
import uk.gov.netz.api.userinfoapi.AuthenticationStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CcaOperatorUserDTO extends UserDTO {
    /** The authentication status. */
    private AuthenticationStatus status;

    @Size(max = 255, message = "{userAccount.jobTitle.typeMismatch}")
    private String jobTitle;

    /** The phone number. */
    @PhoneNumberIntegrity(message = "{userAccount.phoneNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO phoneNumber;

    /** The mobile number. */
    @PhoneNumberIntegrity(message = "{userAccount.mobileNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO mobileNumber;
}
