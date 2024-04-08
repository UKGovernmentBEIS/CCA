package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@Builder
public class AuthorizationCriteria {
    private Long accountId;
    private CompetentAuthorityEnum competentAuthority;
    private Long verificationBodyId;
    private String permission;
}
