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
public class TargetUnitAccountHeaderInfoDTO {

    private String name;
    private String businessId;
    private TargetUnitAccountStatus status;
}
