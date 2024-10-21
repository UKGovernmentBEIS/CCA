package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#energyConsumed != null && #energyConsumed.compareTo(70) < 0) == (#energyConsumedProvision != null)}", 
		message = "underlyingagreement.facilities.apply70rule.energyConsumedProvision")
@SpELExpression(expression = "{(#energyConsumed != null && #energyConsumed.compareTo(70) >= 0 && #energyConsumedEligible != null && #energyConsumedEligible.compareTo(100) == 0)" +
		"|| (#energyConsumed != null && #energyConsumed.compareTo(70) < 0 && #energyConsumedProvision != null && #energyConsumedEligible != null && #energyConsumedEligible.compareTo(#energyConsumed.multiply(#energyConsumedProvision).divide(T(java.math.BigDecimal).valueOf(100L)).add(#energyConsumed).setScale(7, T(java.math.RoundingMode).HALF_UP)) == 0)} ",
		message = "underlyingagreement.facilities.apply70rule.energyConsumedEligible")
@SpELExpression(expression = "{(#energyConsumed != null && #energyConsumed.compareTo(70) < 0) || (#startDate == null)}",
		message = "underlyingagreement.facilities.apply70rule.startDate")
public class Apply70Rule {

	@NotNull
	@DecimalMax(value = "100", inclusive = true)
	@DecimalMin(value = "0", inclusive = true)
	@Digits(integer = 3, fraction = 2)
	private BigDecimal energyConsumed;
	
	@DecimalMax(value = "42.9", inclusive = true)
	@DecimalMin(value = "0", inclusive = true)
	@Digits(integer = 2, fraction = 2)
	private BigDecimal energyConsumedProvision;
	
	@NotNull
	@Digits(integer = Integer.MAX_VALUE, fraction = 7)
	private BigDecimal energyConsumedEligible;
	
	private LocalDate startDate;
	
	@NotNull
	private UUID evidenceFile;
}
