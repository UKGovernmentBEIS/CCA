package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityBaselineAndTargets {
    private LocalDate baselineDate;
    private Boolean isTwelveMonths;
    private BigDecimal energyCarbonFactor;
    private MeasurementType measurementType;
    private Boolean usedReportingMechanism;
    @Builder.Default
    private Map<TargetImprovementType, BigDecimal> improvements = new EnumMap<>(TargetImprovementType.class);
    private BigDecimal totalFixedEnergy;
    private VariableEnergyDepictionType variableEnergyType;
    private BigDecimal baselineVariableEnergy;
    private BigDecimal totalThroughput;
    private String throughputUnit;
    private BigDecimal baselineEnergyCarbonIntensity;
    @Builder.Default
    private List<ProductVariableEnergyConsumptionData> variableEnergyConsumptionDataByProduct = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PerformanceDataFacilityBaselineAndTargets that = (PerformanceDataFacilityBaselineAndTargets) o;

        return ObjectUtils.compare(baselineDate, that.baselineDate) == 0
                && ObjectUtils.compare(isTwelveMonths, that.isTwelveMonths) == 0
                && ObjectUtils.compare(energyCarbonFactor, that.energyCarbonFactor) == 0
                && ObjectUtils.compare(measurementType, that.measurementType) == 0
                && ObjectUtils.compare(usedReportingMechanism, that.usedReportingMechanism) == 0
                && ObjectUtils.compare(improvements.size(), that.improvements.size()) == 0
                && improvements.entrySet().stream().allMatch(e -> ObjectUtils.compare(e.getValue(), that.improvements.get(e.getKey())) == 0)
                && ObjectUtils.compare(totalFixedEnergy, that.totalFixedEnergy) == 0
                && ObjectUtils.compare(variableEnergyType, that.variableEnergyType) == 0
                && ObjectUtils.compare(baselineVariableEnergy, that.baselineVariableEnergy) == 0
                && ObjectUtils.compare(totalThroughput, that.totalThroughput) == 0
                && ObjectUtils.compare(throughputUnit, that.throughputUnit) == 0
                && ObjectUtils.compare(baselineEnergyCarbonIntensity, that.baselineEnergyCarbonIntensity) == 0
                && variableEnergyConsumptionDataByProduct.size() == that.variableEnergyConsumptionDataByProduct.size()
                && new HashSet<>(variableEnergyConsumptionDataByProduct).containsAll(that.variableEnergyConsumptionDataByProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baselineDate, isTwelveMonths, energyCarbonFactor, measurementType, usedReportingMechanism, improvements, totalFixedEnergy,
                variableEnergyType, baselineVariableEnergy, totalThroughput, throughputUnit, baselineEnergyCarbonIntensity, variableEnergyConsumptionDataByProduct);
    }
}
