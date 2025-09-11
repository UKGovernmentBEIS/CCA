package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurplusUpdateDTO {

	@NotNull
	private TargetPeriodType targetPeriodType;

	@NotNull
	@Digits(integer = Integer.MAX_VALUE, fraction = 0)
	@PositiveOrZero
	private BigDecimal newSurplusGained;

	@NotBlank
	@Size(max = 10000)
	private String comments;
}
