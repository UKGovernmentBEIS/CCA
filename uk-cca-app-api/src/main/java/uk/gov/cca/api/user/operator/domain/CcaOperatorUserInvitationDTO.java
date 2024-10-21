package uk.gov.cca.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.ContactTypeRoleCode;
import uk.gov.netz.api.authorization.core.domain.dto.RoleCode;
import uk.gov.netz.api.user.core.domain.dto.UserDTO;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CcaOperatorUserInvitationDTO extends UserDTO {

    @RoleCode(roleType = OPERATOR)
    private String roleCode;

    @ContactTypeRoleCode(roleType = OPERATOR)
    private ContactType contactType;

}
