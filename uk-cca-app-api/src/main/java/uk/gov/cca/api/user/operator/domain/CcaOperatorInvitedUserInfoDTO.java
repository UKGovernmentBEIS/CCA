package uk.gov.cca.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CcaOperatorInvitedUserInfoDTO extends InvitedUserInfoDTO {

    private String firstName;
    private String lastName;
    private String roleCode;
    private String contactType;
    private String accountName;
    private UserInvitationStatus invitationStatus;
}
