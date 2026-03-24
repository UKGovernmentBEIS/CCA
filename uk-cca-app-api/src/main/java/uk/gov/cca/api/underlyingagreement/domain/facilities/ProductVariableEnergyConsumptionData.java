package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#baselineYear == null) " +
        "|| (!T(java.time.Year).parse(#baselineYear).isBefore(T(java.time.Year).of(2022)) && !T(java.time.Year).parse(#baselineYear).isAfter(T(java.time.Year).of(2030)))}",
        message = "underlyingagreement.facilities.facilityEnergyConsumption.ProductVariableEnergyConsumptionData.baselineYear")
public class ProductVariableEnergyConsumptionData {

    @NotNull
    @Size(max = 255)
    private String productName;

    @NotNull
    private Year baselineYear;

    @NotNull
    private ProductStatus productStatus;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energy;

    @NotNull
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal throughput;

    // Throughput unit
    @NotNull
    @Size(max = 255)
    private String throughputUnit;
}
