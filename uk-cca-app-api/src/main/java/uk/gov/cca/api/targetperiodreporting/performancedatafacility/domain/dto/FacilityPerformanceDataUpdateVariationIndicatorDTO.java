package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import java.time.Year;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityPerformanceDataUpdateVariationIndicatorDTO {

	@NotNull
    private Boolean variationIndicator;
	
    @NotNull
    private Year targetPeriodYear;
}
