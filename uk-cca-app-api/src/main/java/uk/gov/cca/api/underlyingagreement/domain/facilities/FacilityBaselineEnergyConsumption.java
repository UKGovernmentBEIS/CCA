package uk.gov.cca.api.underlyingagreement.domain.facilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#hasVariableEnergy == null) " +
        "|| ( (T(java.lang.Boolean).TRUE.equals(#hasVariableEnergy) == (#variableEnergyType != null && (#baselineVariableEnergy != null || #variableEnergyConsumptionDataByProduct?.size() > 0))) " +
        "&& (T(java.lang.Boolean).FALSE.equals(#hasVariableEnergy) == (#variableEnergyType == null && #baselineVariableEnergy == null && #variableEnergyConsumptionDataByProduct?.size() == 0 && #totalThroughput != null && #throughputUnit != null)) )}",
        message = "underlyingagreement.facilities.facilityEnergyConsumption.hasVariableEnergy")
@SpELExpression(expression = "{(#variableEnergyType == null) " +
        "|| ( ((#variableEnergyType eq 'BY_PRODUCT') == (#variableEnergyConsumptionDataByProduct?.size() > 0 && #baselineVariableEnergy == null && #totalThroughput == null && #throughputUnit == null)) " +
        "&& ((#variableEnergyType eq 'TOTALS') == (#baselineVariableEnergy != null && #totalThroughput != null && #throughputUnit != null && #variableEnergyConsumptionDataByProduct?.size() == 0)) )}",
        message = "underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType")
public class FacilityBaselineEnergyConsumption {

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalFixedEnergy;

    @NotNull
    private Boolean hasVariableEnergy;

    private VariableEnergyDepictionType variableEnergyType;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal baselineVariableEnergy;

    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalThroughput;

    @Size(max = 255)
    private String throughputUnit;

    @Builder.Default
    private List<@Valid ProductVariableEnergyConsumptionData> variableEnergyConsumptionDataByProduct = new ArrayList<>();

    @JsonIgnore
    public Optional<BigDecimal> getTotalBaselineVariableEnergy(LocalDate baselineStartDate) {
        LocalDate middleYearDate = baselineStartDate.isLeapYear()
                ? LocalDate.of(baselineStartDate.getYear(), 7, 1)
                : LocalDate.of(baselineStartDate.getYear(), 7, 2);
        int baseYear = baselineStartDate.isAfter(middleYearDate) ? baselineStartDate.getYear() + 1 : baselineStartDate.getYear();
        return Optional.ofNullable(variableEnergyType).map(energyType -> switch (energyType) {
            case TOTALS -> baselineVariableEnergy;
            case BY_PRODUCT -> variableEnergyConsumptionDataByProduct.stream()
                    .filter(p -> p.getProductStatus().equals(ProductStatus.NEW) || p.getProductStatus().equals(ProductStatus.LIVE))
                    .filter(p -> baseYear == p.getBaselineYear().getValue())
                    .map(ProductVariableEnergyConsumptionData::getEnergy)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }
}
