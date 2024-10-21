package uk.gov.cca.api.user.sectoruser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorUserWithAuthorityDTO {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Long userAuthorityId;
    private Long sectorAssociationId;
    private boolean enabled;
}
