package uk.gov.cca.api.workflow.request.application.item.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTargetUnitAccountDTO {

	private Long accountId;

    private String accountName;
    
    private String businessId;

    private CompetentAuthorityEnum competentAuthority;

}
