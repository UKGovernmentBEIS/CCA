package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.domain;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TP6ExcelCellsReferenceEnum;

@Getter
@AllArgsConstructor
public enum TP6ExtractExcelCellsReferenceEnum {
    // Section 1: Report Details
    SECTOR(TP6ExcelCellsReferenceEnum.SECTOR, TP6Data::getSector),
    TARGET_PERIOD(TP6ExcelCellsReferenceEnum.TARGET_PERIOD, TP6Data::getTargetPeriod),
    REPORT_VERSION(TP6ExcelCellsReferenceEnum.REPORT_VERSION, TP6Data::getReportVersion),

    // Section 2: Target Unit Details, Targets and Previous Performance
    TU_IDENTIFIER(TP6ExcelCellsReferenceEnum.TU_IDENTIFIER, TP6Data::getTargetUnitId),
    OPERATOR_NAME(TP6ExcelCellsReferenceEnum.OPERATOR_NAME, TP6Data::getOperatorName),
    NUM_OF_FACILITIES(TP6ExcelCellsReferenceEnum.NUM_OF_FACILITIES, TP6Data::getNumOfFacilities),
    TARGET_TYPE(TP6ExcelCellsReferenceEnum.TARGET_TYPE, TP6Data::getTargetType),
    ENERGY_CARBON_UNIT(TP6ExcelCellsReferenceEnum.ENERGY_CARBON_UNIT, TP6Data::getMeasurementUnit),
    THROUGHPUT_UNIT(TP6ExcelCellsReferenceEnum.THROUGHPUT_UNIT, TP6Data::getThroughputUnit),
    BASE_YEAR_START_DATE(TP6ExcelCellsReferenceEnum.BASE_YEAR_START_DATE, TP6Data::getBaselineDate),
    BASE_YEAR_ENERGY(TP6ExcelCellsReferenceEnum.BASE_YEAR_ENERGY, TP6Data::getBaselineEnergy),
    BASE_YEAR_THROUGHPUT(TP6ExcelCellsReferenceEnum.BASE_YEAR_THROUGHPUT, TP6Data::getBaselineThroughput),
    PERCENT_IMPROVEMENT_TARGET(TP6ExcelCellsReferenceEnum.PERCENT_IMPROVEMENT_TARGET, TP6Data::getImprovement),
    BANKED_SURPLUS_FROM_PREVIOUS_TP(TP6ExcelCellsReferenceEnum.BANKED_SURPLUS_FROM_PREVIOUS_TP, TP6Data::getBankedSurplusFromPreviousTP),

    // Section 5: Supplementary MOA Surplus and Buy-Out Determination
    PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS(TP6ExcelCellsReferenceEnum.PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS, TP6Data::getPreviousBuyOutAfterSurplus),
    PREVIOUS_SURPLUS_USED(TP6ExcelCellsReferenceEnum.PREVIOUS_SURPLUS_USED, TP6Data::getPreviousSurplusUsed),
    SURPLUS_GAINED_IN_TP(TP6ExcelCellsReferenceEnum.SURPLUS_GAINED_IN_TP, TP6Data::getSurplusGainedInTP);

    private final TP6ExcelCellsReferenceEnum referenceEnum;
    private final Function<TP6Data, Object> valueExtractor;

    public Object extractValue(TP6Data tp6Data) {
        return valueExtractor.apply(tp6Data);
    }
}
