package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain;

import static java.math.MathContext.DECIMAL128;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TP6ExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;

@Getter
@AllArgsConstructor
public enum TP6ParseExcelCellsReferenceEnum {

    // Section 1: Report Details
    SECTOR("sector", TP6ExcelCellsReferenceEnum.SECTOR,
            (data, value) -> { data.setSector(value); return null; }),
    TARGET_PERIOD("targetPeriod", TP6ExcelCellsReferenceEnum.TARGET_PERIOD,
            (data, value) -> { data.setTargetPeriod(PerformanceDataTargetPeriodType.fromTargetPeriodType(value)); return null; }),
    REPORT_VERSION("reportVersion", TP6ExcelCellsReferenceEnum.REPORT_VERSION,
            (data, value) -> { data.setReportVersion(Double.valueOf(value).intValue()); return null; }),
    TEMPLATE_VERSION("templateVersion", TP6ExcelCellsReferenceEnum.TEMPLATE_VERSION,
            (data, value) -> { data.setTemplateVersion(value); return null; }),
    REPORT_DATE("reportDate", TP6ExcelCellsReferenceEnum.REPORT_DATE,
            (data, value) -> { data.setReportDate(LocalDate.parse(value)); return null; }),

    // Section 2: Target Unit Details, Targets and Previous Performance
    TU_IDENTIFIER("targetUnitDetails.tuIdentifier", TP6ExcelCellsReferenceEnum.TU_IDENTIFIER,
            (data, value) -> { data.getTargetUnitDetails().setTuIdentifier(value); return null; }),
    OPERATOR_NAME("targetUnitDetails.operatorName", TP6ExcelCellsReferenceEnum.OPERATOR_NAME,
            (data, value) -> { data.getTargetUnitDetails().setOperatorName(value); return null; }),
    NUM_OF_FACILITIES("targetUnitDetails.numOfFacilities", TP6ExcelCellsReferenceEnum.NUM_OF_FACILITIES,
            (data, value) -> { data.getTargetUnitDetails().setNumOfFacilities(toIntegerConversion(value)); return null; }),
    TARGET_TYPE("targetType", TP6ExcelCellsReferenceEnum.TARGET_TYPE,
            (data, value) -> { data.setTargetType(AgreementCompositionType.fromDescription(value)); return null; }),
    ENERGY_CARBON_UNIT("targetUnitDetails.energyCarbonUnit", TP6ExcelCellsReferenceEnum.ENERGY_CARBON_UNIT,
            (data, value) -> { data.getTargetUnitDetails().setEnergyCarbonUnit(MeasurementType.getMeasurementTypeByUnit(value)); return null; }),
    THROUGHPUT_UNIT("targetUnitDetails.throughputUnit", TP6ExcelCellsReferenceEnum.THROUGHPUT_UNIT,
            (data, value) -> { data.getTargetUnitDetails().setThroughputUnit(value); return null; }),
    BASE_YEAR_START_DATE("targetUnitDetails.byStartDate", TP6ExcelCellsReferenceEnum.BASE_YEAR_START_DATE,
            (data, value) -> { data.getTargetUnitDetails().setByStartDate(LocalDate.parse(value)); return null; }),
    BASE_YEAR_ENERGY("targetUnitDetails.byEnergyCarbon", TP6ExcelCellsReferenceEnum.BASE_YEAR_ENERGY,
            (data, value) -> { data.getTargetUnitDetails().setByEnergyCarbon(new BigDecimal(value, DECIMAL128).setScale(7, RoundingMode.DOWN)); return null; }),
    BASE_YEAR_THROUGHPUT("targetUnitDetails.byThroughput", TP6ExcelCellsReferenceEnum.BASE_YEAR_THROUGHPUT,
            (data, value) -> { data.getTargetUnitDetails().setByThroughput(new BigDecimal(value, DECIMAL128).setScale(7, RoundingMode.DOWN)); return null; }),
    BASE_YEAR_PERFORMANCE("targetUnitDetails.byPerformance", TP6ExcelCellsReferenceEnum.BASE_YEAR_PERFORMANCE,
            (data, value) -> { data.getTargetUnitDetails().setByPerformance(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    NUMERICAL_TARGET("targetUnitDetails.numericalTarget", TP6ExcelCellsReferenceEnum.NUMERICAL_TARGET,
            (data, value) -> { data.getTargetUnitDetails().setNumericalTarget(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    TOLERANCE("targetUnitDetails.tolerance", TP6ExcelCellsReferenceEnum.TOLERANCE,
            (data, value) -> { data.getTargetUnitDetails().setTolerance(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    PERCENT_IMPROVEMENT_TARGET("targetUnitDetails.percentTarget", TP6ExcelCellsReferenceEnum.PERCENT_IMPROVEMENT_TARGET,
            (data, value) -> { data.getTargetUnitDetails().setPercentTarget(new BigDecimal(value, DECIMAL128).setScale(9, RoundingMode.DOWN)); return null; }),
    TOLERANCE_PERCENTAGE("targetUnitDetails.tolerancePercentage", TP6ExcelCellsReferenceEnum.TOLERANCE_PERCENTAGE,
            (data, value) -> { data.getTargetUnitDetails().setTolerancePercentage(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    BANKED_SURPLUS_FROM_PREVIOUS_TP("targetUnitDetails.bankedSurplus", TP6ExcelCellsReferenceEnum.BANKED_SURPLUS_FROM_PREVIOUS_TP,
            (data, value) -> { data.getTargetUnitDetails().setBankedSurplus(new BigDecimal(value, DECIMAL128).setScale(7, RoundingMode.DOWN)); return null; }),

    // Section 3: Actual Target Period Performance for Target Facility
    ACTUAL_TU_IDENTIFIER("actualTargetPeriodPerformance.actualTuIdentifier", TP6ExcelCellsReferenceEnum.ACTUAL_TU_IDENTIFIER,
            (data, value) -> { data.getActualTargetPeriodPerformance().setActualTuIdentifier(value); return null; }),
    ACTUAL_THROUGHPUT("actualTargetPeriodPerformance.actualThroughput", TP6ExcelCellsReferenceEnum.ACTUAL_THROUGHPUT,
            (data, value) -> { data.getActualTargetPeriodPerformance().setActualThroughput(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    ELECTRICITY_USED("actualTargetPeriodPerformance.energyData[ELECTRICITY]", TP6ExcelCellsReferenceEnum.ELECTRICITY_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.ELECTRICITY, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    NATURAL_GAS_USED("actualTargetPeriodPerformance.energyData[NATURAL_GAS]", TP6ExcelCellsReferenceEnum.NATURAL_GAS_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.NATURAL_GAS, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    FUEL_OIL_USED("actualTargetPeriodPerformance.energyData[FUEL_OIL]", TP6ExcelCellsReferenceEnum.FUEL_OIL_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.FUEL_OIL, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    COAL_USED("actualTargetPeriodPerformance.energyData[COAL]", TP6ExcelCellsReferenceEnum.COAL_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.COAL, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    COKE_USED("actualTargetPeriodPerformance.energyData[COKE]", TP6ExcelCellsReferenceEnum.COKE_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.COKE, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    LPG_USED("actualTargetPeriodPerformance.energyData[LPG]", TP6ExcelCellsReferenceEnum.LPG_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.LPG, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    ETHANE_USED("actualTargetPeriodPerformance.energyData[ETHANE]", TP6ExcelCellsReferenceEnum.ETHANE_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.ETHANE, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    KEROSENE_USED("actualTargetPeriodPerformance.energyData[KEROSENE]", TP6ExcelCellsReferenceEnum.KEROSENE_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.KEROSENE, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    PETROL_USED("actualTargetPeriodPerformance.energyData[PETROL]", TP6ExcelCellsReferenceEnum.PETROL_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.PETROL, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    GAS_DIESEL_OIL_USED("actualTargetPeriodPerformance.energyData[GAS_DIESEL_OIL]", TP6ExcelCellsReferenceEnum.GAS_DIESEL_OIL_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.GAS_DIESEL_OIL, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    NAPHTHA_USED("actualTargetPeriodPerformance.energyData[NAPHTHA]", TP6ExcelCellsReferenceEnum.NAPHTHA_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.NAPHTHA, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    PETROLEUM_COKE_USED("actualTargetPeriodPerformance.energyData[PETROLEUM_COKE]", TP6ExcelCellsReferenceEnum.PETROLEUM_COKE_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.PETROLEUM_COKE, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    REFINERY_GAS_USED("actualTargetPeriodPerformance.energyData[REFINERY_GAS]", TP6ExcelCellsReferenceEnum.REFINERY_GAS_USED,
            (data, value) -> { data.getActualTargetPeriodPerformance().getEnergyData().put(FixedConversionFactor.REFINERY_GAS, new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    
    
    OTHER_FUEL_USED_1_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[0].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_1_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(0).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_1_NAME("actualTargetPeriodPerformance.carbonFactors[0].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_1_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(0).setName(value); return null;}),
    OTHER_FUEL_USED_1_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[0].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_1_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(0).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_2_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[1].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_2_CONSUMPTION,
        (data, value) -> { OtherFuel fuel = data.getActualTargetPeriodPerformance().getCarbonFactors().get(1); fuel.setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_2_NAME("actualTargetPeriodPerformance.carbonFactors[1].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_2_NAME,
            (data, value) -> { OtherFuel fuel = data.getActualTargetPeriodPerformance().getCarbonFactors().get(1); fuel.setName(value); return null;}),
    OTHER_FUEL_USED_2_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[1].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_2_CONVERSION_FACTOR,
            (data, value) -> { OtherFuel fuel = data.getActualTargetPeriodPerformance().getCarbonFactors().get(1); fuel.setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_3_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[2].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_3_CONSUMPTION,
        (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(2).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_3_NAME("actualTargetPeriodPerformance.carbonFactors[2].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_3_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(2).setName(value); return null;}),
    OTHER_FUEL_USED_3_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[2].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_3_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(2).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_4_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[3].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_4_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(3).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_4_NAME("actualTargetPeriodPerformance.carbonFactors[3].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_4_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(3).setName(value); return null;}),
    OTHER_FUEL_USED_4_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[3].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_4_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(3).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_5_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[4].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_5_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(4).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_5_NAME("actualTargetPeriodPerformance.carbonFactors[4].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_5_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(4).setName(value); return null;}),
    OTHER_FUEL_USED_5_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[4].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_5_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(4).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_6_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[5].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_6_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(5).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN));return null;}),
    OTHER_FUEL_USED_6_NAME("actualTargetPeriodPerformance.carbonFactors[5].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_6_NAME,
            (data, value) -> {data.getActualTargetPeriodPerformance().getCarbonFactors().get(5).setName(value); return null;}),
    OTHER_FUEL_USED_6_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[5].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_6_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(5).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_7_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[6].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_7_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(6).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_7_NAME("actualTargetPeriodPerformance.carbonFactors[6].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_7_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(6).setName(value); return null;}),
    OTHER_FUEL_USED_7_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[6].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_7_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(6).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_8_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[7].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_8_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(7).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_8_NAME("actualTargetPeriodPerformance.carbonFactors[7].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_8_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(7).setName(value); return null;}),
    OTHER_FUEL_USED_8_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[7].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_8_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(7).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_9_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[8].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_9_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(8).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_9_NAME("actualTargetPeriodPerformance.carbonFactors[8].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_9_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(8).setName(value); return null;}),
    OTHER_FUEL_USED_9_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[8].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_9_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(8).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_10_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[9].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_10_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(9).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_10_NAME("actualTargetPeriodPerformance.carbonFactors[9].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_10_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(9).setName(value); return null;}),
    OTHER_FUEL_USED_10_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[9].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_10_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(9).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    OTHER_FUEL_USED_11_CONSUMPTION("actualTargetPeriodPerformance.carbonFactors[10].consumption", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_11_CONSUMPTION,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(10).setConsumption(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    OTHER_FUEL_USED_11_NAME("actualTargetPeriodPerformance.carbonFactors[10].name", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_11_NAME,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(10).setName(value); return null;}),
    OTHER_FUEL_USED_11_CONVERSION_FACTOR("actualTargetPeriodPerformance.carbonFactors[10].conversionFactor", TP6ExcelCellsReferenceEnum.OTHER_FUEL_USED_11_CONVERSION_FACTOR,
            (data, value) -> { data.getActualTargetPeriodPerformance().getCarbonFactors().get(10).setConversionFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null;}),
    
    
    TOTAL_ENERGY("actualTargetPeriodPerformance.tpEnergy", TP6ExcelCellsReferenceEnum.TOTAL_ENERGY,
            (data, value) -> { data.getActualTargetPeriodPerformance().setTpEnergy(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    CHP_DELIVERED_ELECTRICITY("actualTargetPeriodPerformance.tpChpDeliveredElectricity", TP6ExcelCellsReferenceEnum.CHP_DELIVERED_ELECTRICITY,
            (data, value) -> { data.getActualTargetPeriodPerformance().setTpChpDeliveredElectricity(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    REPORTING_THROUGHPUT("actualTargetPeriodPerformance.reportingThroughput", TP6ExcelCellsReferenceEnum.REPORTING_THROUGHPUT,
            (data, value) -> { data.getActualTargetPeriodPerformance().setReportingThroughput(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    ADJUSTED_THROUGHPUT("actualTargetPeriodPerformance.adjustedThroughput", TP6ExcelCellsReferenceEnum.ADJUSTED_THROUGHPUT,
            (data, value) -> { data.getActualTargetPeriodPerformance().setAdjustedThroughput(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),

    // Section 4: Target Period Performance Result
    TARGET_ENERGY_CARBON_THROUGHPUT("performanceResult.targetEnergyCarbonTpThroughput", TP6ExcelCellsReferenceEnum.TARGET_ENERGY_CARBON_THROUGHPUT,
            (data, value) -> { data.getPerformanceResult().setTargetEnergyCarbonTpThroughput(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    ENERGY_CARBON_THROUGHPUT("performanceResult.byEnergyCarbonTpThroughput", TP6ExcelCellsReferenceEnum.ENERGY_CARBON_THROUGHPUT,
            (data, value) -> { data.getPerformanceResult().setByEnergyCarbonTpThroughput(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    TARGET_PERIOD_ENERGY("performanceResult.tpPerformance", TP6ExcelCellsReferenceEnum.TARGET_PERIOD_ENERGY,
            (data, value) -> { data.getPerformanceResult().setTpPerformance(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    TARGET_PERIOD_IMPROVEMENT_PERCENTAGE("performanceResult.tpPerformancePercent", TP6ExcelCellsReferenceEnum.TARGET_PERIOD_IMPROVEMENT_PERCENTAGE,
            (data, value) -> { data.getPerformanceResult().setTpPerformancePercent(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    TARGET_PERIOD_RESULT("performanceResult.tpOutcome", TP6ExcelCellsReferenceEnum.TARGET_PERIOD_RESULT,
            (data, value) -> { data.getPerformanceResult().setTpOutcome(TargetPeriodResultType.fromDescription(value)); return null; }),

    // Section 5: Carbon Surplus or Buy-Out Determination
    TARGET_PERIOD_CARBON_FACTOR("primaryDetermination.tpCarbonFactor", TP6ExcelCellsReferenceEnum.TARGET_PERIOD_CARBON_FACTOR,
            (data, value) -> { data.getPrimaryDetermination().setTpCarbonFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    AMOUNT_ENERGY_USED_UNDER_TARGET("primaryDetermination.energyCarbonUnderTarget", TP6ExcelCellsReferenceEnum.AMOUNT_ENERGY_USED_UNDER_TARGET,
            (data, value) -> { data.getPrimaryDetermination().setEnergyCarbonUnderTarget(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    AMOUNT_CO2_EMITTED_UNDER_TARGET("primaryDetermination.carbonUnderTarget", TP6ExcelCellsReferenceEnum.AMOUNT_CO2_EMITTED_UNDER_TARGET,
            (data, value) -> { data.getPrimaryDetermination().setCarbonUnderTarget(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    CARBON_DIOXIDE_EMITTED("primaryDetermination.co2Emissions", TP6ExcelCellsReferenceEnum.CARBON_DIOXIDE_EMITTED,
            (data, value) -> { data.getPrimaryDetermination().setCo2Emissions(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    BANKED_SURPLUS_USED("primaryDetermination.surplusUsed", TP6ExcelCellsReferenceEnum.BANKED_SURPLUS_USED,
            (data, value) -> { data.getPrimaryDetermination().setSurplusUsed(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    SURPLUS_GAINED("primaryDetermination.surplusGained", TP6ExcelCellsReferenceEnum.SURPLUS_GAINED,
            (data, value) -> { data.getPrimaryDetermination().setSurplusGained(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    BUY_OUT_REQUIRED("primaryDetermination.priBuyOutCarbon", TP6ExcelCellsReferenceEnum.BUY_OUT_REQUIRED,
            (data, value) -> { data.getPrimaryDetermination().setPriBuyOutCarbon(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    BUY_OUT_COST("primaryDetermination.priBuyOutCost", TP6ExcelCellsReferenceEnum.BUY_OUT_COST,
            (data, value) -> { data.getPrimaryDetermination().setPriBuyOutCost(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),

    // Section 5: Supplementary MOA Surplus and Buy-Out Determination
    SUPPLEMENTARY_TARGET_PERIOD_CARBON_FACTOR("secondaryDetermination.tpCarbonFactor", TP6ExcelCellsReferenceEnum.SUPPLEMENTARY_TARGET_PERIOD_CARBON_FACTOR,
            (data, value) -> { data.getSecondaryDetermination().setTpCarbonFactor(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    SUPPLEMENTARY_AMOUNT_ENERGY_USED_UNDER_TARGET("secondaryDetermination.energyCarbonUnderTarget", TP6ExcelCellsReferenceEnum.SUPPLEMENTARY_AMOUNT_ENERGY_USED_UNDER_TARGET,
            (data, value) -> { data.getSecondaryDetermination().setEnergyCarbonUnderTarget(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    SUPPLEMENTARY_AMOUNT_CO2_EMITTED_UNDER_TARGET("secondaryDetermination.carbonUnderTarget", TP6ExcelCellsReferenceEnum.SUPPLEMENTARY_AMOUNT_CO2_EMITTED_UNDER_TARGET,
            (data, value) -> { data.getSecondaryDetermination().setCarbonUnderTarget(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    SUPPLEMENTARY_CARBON_DIOXIDE_EMITTED("secondaryDetermination.co2Emissions", TP6ExcelCellsReferenceEnum.SUPPLEMENTARY_CARBON_DIOXIDE_EMITTED,
            (data, value) -> { data.getSecondaryDetermination().setCo2Emissions(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    TOTAL_TARGET_PERIOD_BUY_OUT_REQUIRED("secondaryDetermination.priBuyOutCarbon", TP6ExcelCellsReferenceEnum.TOTAL_TARGET_PERIOD_BUY_OUT_REQUIRED,
            (data, value) -> { data.getSecondaryDetermination().setPriBuyOutCarbon(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS("secondaryDetermination.prevBuyOutCo2", TP6ExcelCellsReferenceEnum.PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS,
            (data, value) -> { data.getSecondaryDetermination().setPrevBuyOutCo2(new BigDecimal(value, DECIMAL128).setScale(7, RoundingMode.DOWN)); return null; }),
    PREVIOUS_SURPLUS_USED("secondaryDetermination.prevSurplusUsed", TP6ExcelCellsReferenceEnum.PREVIOUS_SURPLUS_USED,
            (data, value) -> { data.getSecondaryDetermination().setPrevSurplusUsed(new BigDecimal(value, DECIMAL128).setScale(7, RoundingMode.DOWN)); return null; }),
    SURPLUS_GAINED_IN_TP("secondaryDetermination.prevSurplusGained", TP6ExcelCellsReferenceEnum.SURPLUS_GAINED_IN_TP,
            (data, value) -> { data.getSecondaryDetermination().setPrevSurplusGained(new BigDecimal(value, DECIMAL128).setScale(7, RoundingMode.DOWN)); return null; }),
    SECONDARY_BUY_OUT_REQUIRED("secondaryDetermination.secondaryBuyOutCo2", TP6ExcelCellsReferenceEnum.SECONDARY_BUY_OUT_REQUIRED,
            (data, value) -> { data.getSecondaryDetermination().setSecondaryBuyOutCo2(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    SECONDARY_BUY_OUT_COST("secondaryDetermination.secondaryBuyOutCost", TP6ExcelCellsReferenceEnum.SECONDARY_BUY_OUT_COST,
            (data, value) -> { data.getSecondaryDetermination().setSecondaryBuyOutCost(new BigDecimal(value, DECIMAL128).setScale(20, RoundingMode.DOWN)); return null; }),
    ;

    private final String property;
    private final TP6ExcelCellsReferenceEnum referenceEnum;
    private final BiFunction<TP6PerformanceData, String, Void> valueParser;

    public static String getExcelRowColumn(String property) {
        return Arrays.stream(TP6ParseExcelCellsReferenceEnum.values())
                .filter(ref -> ref.property.equals(property))
                .findFirst()
                .map(ref -> PerformanceDataUploadUtility.getExcelCell(ref.referenceEnum.getRowIndex(), ref.referenceEnum.getColumnIndex()))
                .orElse("");
    }

    public Object parseValue(TP6PerformanceData data, String value) {
        return valueParser.apply(data, value);
    }
    
    public static Integer toIntegerConversion(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            double doubleValue = Double.parseDouble(value);
            
            return (doubleValue % 1 == 0) ? (int) doubleValue : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
