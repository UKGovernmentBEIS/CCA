package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountPerformanceDataUpdateLockDTO {

    @NotNull
    private Boolean locked;

    @NotNull
    private TargetPeriodType targetPeriodType;
}
