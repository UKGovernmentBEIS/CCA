package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPerformanceDataInfo {
    private Long accountId;
    private String accountBusinessId;
    private Long lastPerformanceDataId;
}
