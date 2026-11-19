package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetPeriodBuyOutCostUpdateDTO {

	@NotNull
	@Positive
	@Digits(integer = Integer.MAX_VALUE, fraction = 2)
	private BigDecimal buyOutCost;
}
