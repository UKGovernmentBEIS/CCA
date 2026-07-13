package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import java.time.Year;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityPerformanceDataUpdateLockDTO {

	@NotNull
    private Boolean locked;

	@NotNull
	private TargetPeriodType targetPeriodType;
	
    @NotNull
    private Year targetPeriodYear;
}
