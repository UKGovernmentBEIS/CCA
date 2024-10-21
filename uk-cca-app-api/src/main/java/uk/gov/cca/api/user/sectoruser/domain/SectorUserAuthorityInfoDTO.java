package uk.gov.cca.api.user.sectoruser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorUserAuthorityInfoDTO {

    private String firstName;
    private String lastName;
    private String roleName;
    private String roleCode;
    private String contactType;
    private AuthorityStatus status;
    private String userId;
}
