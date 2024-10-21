package uk.gov.cca.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountInfoDTO {
	
    private Long accountId;
    private String businessId;
    private String accountName;
    private TargetUnitAccountStatus status;
    private String siteContactUserId;
}
