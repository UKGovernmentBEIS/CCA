package uk.gov.cca.api.user.verifier.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.core.domain.dto.RoleCode;
import uk.gov.cca.api.user.core.domain.dto.UserDTO;
import uk.gov.netz.api.common.domain.RoleType;

/**
 * Data transfer object used to invite a verifier user to join the system.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class VerifierUserInvitationDTO extends UserDTO {

    @RoleCode(roleType = RoleType.VERIFIER)
    private String roleCode;

    @NotBlank(message = "{phoneNumber.number.notEmpty}")
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String phoneNumber;

    /** The mobile number. */
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String mobileNumber;
}
