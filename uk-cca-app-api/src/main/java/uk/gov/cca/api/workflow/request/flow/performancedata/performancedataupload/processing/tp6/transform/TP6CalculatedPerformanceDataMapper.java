package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils.ActualTargetPeriodPerformanceCalculationUtil;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils.PrimaryDeterminationCalculationUtil;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils.SecondaryDeterminationCalculationUtil;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils.TargetPeriodPerformanceResultCalculationUtil;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils.TargetUnitDetailsCalculationUtil;
import uk.gov.netz.api.common.config.MapperConfig;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TP6CalculatedPerformanceDataMapper {

    @Mapping(target = "energyCarbonUnit", source = "targetUnitDetails.energyCarbonUnit")
    @Mapping(target = "byEnergyCarbon", source = "targetUnitDetails.byEnergyCarbon")
    @Mapping(target = "byThroughput", source = "targetUnitDetails.byThroughput")
    @Mapping(target = "percentTarget", source = "targetUnitDetails.percentTarget")
    @Mapping(target = "tolerancePercentage", source = "targetUnitDetails.tolerancePercentage")
    @Mapping(target = "bankedSurplus", source = "targetUnitDetails.bankedSurplus")
    @Mapping(target = "actualThroughput", source = "actualTargetPeriodPerformance.actualThroughput")
    @Mapping(target = "energyData", source = "actualTargetPeriodPerformance.energyData")
    @Mapping(target = "carbonFactors", source = "actualTargetPeriodPerformance.carbonFactors")
    @Mapping(target = "reportingThroughput", source = "actualTargetPeriodPerformance.reportingThroughput")
    @Mapping(target = "adjustedThroughput", source = "actualTargetPeriodPerformance.adjustedThroughput")
    @Mapping(target = "targetEnergyCarbonTpThroughput", source = "performanceResult.targetEnergyCarbonTpThroughput")
    @Mapping(target = "byEnergyCarbonTpThroughput", source = "performanceResult.byEnergyCarbonTpThroughput")
    @Mapping(target = "prevBuyOutCo2", source = "secondaryDetermination.prevBuyOutCo2")
    @Mapping(target = "prevSurplusUsed", source = "secondaryDetermination.prevSurplusUsed")
    @Mapping(target = "prevSurplusGained", source = "secondaryDetermination.prevSurplusGained")
    PerformanceDataCalculationParameters toPrepopulatedAndInputPerformanceData(TP6PerformanceData performanceData);

    @Mapping(target = "byPerformance", source = "parameters", qualifiedByName = "getByPerformance")
    @Mapping(target = "numericalTarget", source = "parameters", qualifiedByName = "getNumericalTarget")
    @Mapping(target = "tolerance", source = "parameters", qualifiedByName = "getTolerance")
    @Mapping(target = "tpEnergy", source = "parameters", qualifiedByName = "getTpEnergy")
    @Mapping(target = "tpChpDeliveredElectricity", source = "parameters", qualifiedByName = "getTpChpDeliveredElectricity")
    @Mapping(target = "tpPerformance", source = "parameters", qualifiedByName = "getTpPerformance")
    @Mapping(target = "tpPerformancePercent", source = "parameters", qualifiedByName = "getTpPerformancePercent")
    @Mapping(target = "tpOutcome", source = "parameters", qualifiedByName = "getTpOutcome")
    @Mapping(target = "tpCarbonFactor", source = "parameters", qualifiedByName = "getTpCarbonFactor")
    @Mapping(target = "energyCarbonUnderTarget", source = "parameters", qualifiedByName = "getEnergyCarbonUnderTarget")
    @Mapping(target = "carbonUnderTarget", source = "parameters", qualifiedByName = "getCarbonUnderTarget")
    @Mapping(target = "co2Emissions", source = "parameters", qualifiedByName = "getCo2Emissions")
    @Mapping(target = "surplusUsed", source = "parameters", qualifiedByName = "getSurplusUsed")
    @Mapping(target = "surplusGained", source = "parameters", qualifiedByName = "getSurplusGained")
    @Mapping(target = "priBuyOutCarbon", source = "parameters", qualifiedByName = "getPriBuyOutCarbon")
    @Mapping(target = "priBuyOutCost", source = "parameters", qualifiedByName = "getPriBuyOutCost")
    @Mapping(target = "secondaryBuyOutCo2", source = "parameters", qualifiedByName = "getSecondaryBuyOutCo2")
    @Mapping(target = "secondaryBuyOutCost", source = "parameters", qualifiedByName = "getSecondaryBuyOutCost")
    PerformanceDataCalculatedMetrics toCalculatedPerformanceData(PerformanceDataCalculationParameters parameters);

    @Named("getByPerformance")
    default BigDecimal getByPerformance(PerformanceDataCalculationParameters parameters) {
        return TargetUnitDetailsCalculationUtil.getByPerformanceCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getNumericalTarget")
    default BigDecimal getNumericalTarget(PerformanceDataCalculationParameters parameters) {
        return TargetUnitDetailsCalculationUtil.getNumericalTargetCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getTolerance")
    default BigDecimal getTolerance(PerformanceDataCalculationParameters parameters) {
        return TargetUnitDetailsCalculationUtil.getToleranceCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getTpEnergy")
    default BigDecimal getTpEnergy(PerformanceDataCalculationParameters parameters) {
        return ActualTargetPeriodPerformanceCalculationUtil.getTpEnergyCalculatedValue(parameters);
    }

    @Named("getTpChpDeliveredElectricity")
    default BigDecimal getTpChpDeliveredElectricity(PerformanceDataCalculationParameters parameters) {
        return ActualTargetPeriodPerformanceCalculationUtil.getTpChpDeliveredElectricityCalculatedValue(parameters);
    }

    @Named("getTpPerformance")
    default BigDecimal getTpPerformance(PerformanceDataCalculationParameters parameters) {
        return TargetPeriodPerformanceResultCalculationUtil.getTpPerformanceCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getTpPerformancePercent")
    default BigDecimal getTpPerformancePercent(PerformanceDataCalculationParameters parameters) {
        return TargetPeriodPerformanceResultCalculationUtil.getTpPerformancePercentCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getTpOutcome")
    default TargetPeriodResultType getTpOutcome(PerformanceDataCalculationParameters parameters) {
        return TargetPeriodPerformanceResultCalculationUtil.getTpOutcomeCalculatedValue(parameters);
    }

    @Named("getTpCarbonFactor")
    default BigDecimal getTpCarbonFactor(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getTpCarbonFactorCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getEnergyCarbonUnderTarget")
    default BigDecimal getEnergyCarbonUnderTarget(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getEnergyCarbonUnderTargetCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getCarbonUnderTarget")
    default BigDecimal getCarbonUnderTarget(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getCarbonUnderTargetCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getCo2Emissions")
    default BigDecimal getCo2Emissions(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getCo2EmissionsCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getSurplusUsed")
    default BigDecimal getSurplusUsed(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getSurplusUsedCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getSurplusGained")
    default BigDecimal getSurplusGained(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getSurplusGainedCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getPriBuyOutCarbon")
    default BigDecimal getPriBuyOutCarbon(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getPriBuyOutCarbonCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getPriBuyOutCost")
    default BigDecimal getPriBuyOutCost(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationUtil.getPriBuyOutCostCalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getSecondaryBuyOutCo2")
    default BigDecimal getSecondaryBuyOutCo2(PerformanceDataCalculationParameters parameters) {
        return SecondaryDeterminationCalculationUtil.getSecondaryBuyOutCo2CalculatedValue(parameters)
                .orElse(null);
    }

    @Named("getSecondaryBuyOutCost")
    default BigDecimal getSecondaryBuyOutCost(PerformanceDataCalculationParameters parameters) {
        return SecondaryDeterminationCalculationUtil.getSecondaryBuyOutCostCalculatedValue(parameters)
                .orElse(null);
    }
}
