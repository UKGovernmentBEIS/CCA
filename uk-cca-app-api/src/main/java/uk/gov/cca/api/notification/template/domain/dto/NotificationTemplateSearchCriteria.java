package uk.gov.cca.api.notification.template.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateSearchCriteria {

    private CompetentAuthorityEnum competentAuthority;
    private String term;
    private RoleType roleType;
    private PagingRequest paging;
}
