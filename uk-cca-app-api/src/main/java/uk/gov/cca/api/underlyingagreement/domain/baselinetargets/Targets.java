package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Targets {

	@NotNull
	@DecimalMax(value = "100")
	@Digits(integer = 3, fraction = 7)
	private BigDecimal improvement;

	@PositiveOrZero
	@Digits(integer = Integer.MAX_VALUE, fraction = 7)
	private BigDecimal target;
}
