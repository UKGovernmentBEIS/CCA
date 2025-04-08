package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPerformanceDataStatusDTO {

  private Long id;
  private TargetPeriodDTO targetPeriod;
  private Long accountId;
  private boolean locked;
  private Long lastPerformanceDataId;
}

