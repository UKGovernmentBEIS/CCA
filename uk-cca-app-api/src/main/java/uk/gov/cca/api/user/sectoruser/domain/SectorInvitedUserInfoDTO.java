package uk.gov.cca.api.user.sectoruser.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SectorInvitedUserInfoDTO extends InvitedUserInfoDTO {

    private String firstName;
    private String lastName;
    private String roleCode;
    private UserInvitationStatus invitationStatus;
    private ContactType contactType;
    private Long sectorAssociationId;
    private String sector;
}
