package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils.PerformanceDataFacilityCalculationFunctionUtil;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class PerformanceDataFacilityCalculationMapper {

    public PerformanceDataFacilityCalculatedResults toPerformanceDataFacilityCalculatedResults(PerformanceDataFacilityCalculationParameters parameters,
                                                                                               PerformanceDataFacilityInputData performanceData) {
        // Calculate
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(performanceData.getEnergyFuelDetails(), parameters.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(parameters, performanceData);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal targetImprovement = PerformanceDataFacilityCalculationFunctionUtil
                .TARGET_IMPROVEMENT.apply(parameters).setScale(7, RoundingMode.HALF_UP);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(performanceData.getEnergyFuelDetails(), actualEnergyCarbon, parameters.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_TCO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, parameters.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_TCO2_EMISSIONS
                .apply(performanceData.getEnergyFuelDetails(), parameters.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.TCO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, parameters, performanceData);

        // Calculate for FINAL report
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = parameters.getReportType().equals(PerformanceDataReportType.FINAL)
                ? PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE.apply(energyCarbonDifference)
                : null;
        BigDecimal surplusGained = parameters.getReportType().equals(PerformanceDataReportType.FINAL)
                ? PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference)
                : null;
        BigDecimal buyOutRequired = parameters.getReportType().equals(PerformanceDataReportType.FINAL)
                ? PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference)
                : null;

        return PerformanceDataFacilityCalculatedResults.builder()
                .actualEnergyCarbon(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .targetEnergyCarbon(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .energyCarbonDifference(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .targetImprovement(targetImprovement.setScale(7, RoundingMode.HALF_UP))
                .weightedConversionFactor(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .targetCo2Emissions(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .actualCo2Emissions(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .co2EmissionsDifference(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .actualImprovement(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .targetPeriodResultType(targetPeriodResultType)
                .surplusGained(surplusGained)
                .buyOutRequired(buyOutRequired)
                .build();
    }
}
