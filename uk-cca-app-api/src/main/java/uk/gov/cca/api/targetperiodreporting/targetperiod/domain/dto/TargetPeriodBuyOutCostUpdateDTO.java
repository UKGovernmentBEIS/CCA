package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetPeriodBuyOutCostUpdateDTO {

	@NotNull
	@Positive
	private Integer buyOutCost;
}
