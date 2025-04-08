package uk.gov.cca.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountBusinessInfoDTO {
    private Long accountId;
    private String businessId;
}
