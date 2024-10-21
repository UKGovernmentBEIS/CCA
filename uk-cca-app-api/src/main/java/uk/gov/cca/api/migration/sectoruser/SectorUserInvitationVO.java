package uk.gov.cca.api.migration.sectoruser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorUserInvitationVO {
    private Long rowId;

    private String firstName;
    private String lastName;
    private String email;
    private ContactType contactType;
    private String roleCode;

    private String sectorAcronym;

    private String inviterEmail;

}
