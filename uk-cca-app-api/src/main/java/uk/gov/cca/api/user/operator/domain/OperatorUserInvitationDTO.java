package uk.gov.cca.api.user.operator.domain;

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
 * Data transfer object used to add an operator user to an account.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OperatorUserInvitationDTO extends UserDTO {

    @RoleCode(roleType = RoleType.OPERATOR)
    private String roleCode;
}
