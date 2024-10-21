package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
	@Digits(integer = 3, fraction = 3)
	private BigDecimal improvement;
	
	@Digits(integer = Integer.MAX_VALUE, fraction = 7)
	private BigDecimal target;
}
