package uk.gov.cca.api.underlyingagreement.domain.facilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Objects;

@Getter
@Setter
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public BigDecimal getEnergyCarbonIntensity() {
        if (energy == null || throughput == null || throughput.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return energy.divide(throughput, MathContext.DECIMAL128);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProductVariableEnergyConsumptionData that = (ProductVariableEnergyConsumptionData) o;

        return ObjectUtils.compare(productName, that.productName) == 0
                && ObjectUtils.compare(baselineYear, that.baselineYear) == 0
                && ObjectUtils.compare(productStatus, that.productStatus) == 0
                && ObjectUtils.compare(energy, that.energy) == 0
                && ObjectUtils.compare(throughput, that.throughput) == 0
                && ObjectUtils.compare(throughputUnit, that.throughputUnit) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, baselineYear, productStatus,  energy == null ? null : energy.stripTrailingZeros(),
                throughput == null ? null : throughput.stripTrailingZeros(), throughputUnit);
    }
}
