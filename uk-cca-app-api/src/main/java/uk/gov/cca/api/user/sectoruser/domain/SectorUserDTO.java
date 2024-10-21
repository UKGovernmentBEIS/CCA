package uk.gov.cca.api.user.sectoruser.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.validation.PhoneNumberIntegrity;
import uk.gov.netz.api.user.core.domain.dto.UserDTO;
import uk.gov.netz.api.userinfoapi.AuthenticationStatus;

/**
 * The sector user's details DTO.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SectorUserDTO extends UserDTO {

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
