package uk.gov.cca.api.authorization.rules.services.resource;

import lombok.Builder;
import lombok.Data;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@Builder
public class ResourceCriteria {
    
    private Long accountId;
    private CompetentAuthorityEnum competentAuthority;
    private Long verificationBodyId;
    
}
