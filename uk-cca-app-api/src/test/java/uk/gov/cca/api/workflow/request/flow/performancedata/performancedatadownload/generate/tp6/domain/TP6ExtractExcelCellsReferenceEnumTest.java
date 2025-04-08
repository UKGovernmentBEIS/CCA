package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TP6ExtractExcelCellsReferenceEnumTest {

  private TP6Data tp6Data;
  private Map<TP6ExtractExcelCellsReferenceEnum, Object> expectedValues;

  @BeforeEach
  void setUp() {
    tp6Data = createDefaultTP6Data();
    expectedValues = createExpectedValuesMap(tp6Data);
  }

  @Test
  void testExtractValues() {
    for (TP6ExtractExcelCellsReferenceEnum reference : TP6ExtractExcelCellsReferenceEnum.values()) {
      Object actual = reference.extractValue(tp6Data);
      Object expected = expectedValues.get(reference);
      assertEquals(expected, actual, "Unexpected value for " + reference.name());
    }
  }

  private TP6Data createDefaultTP6Data() {
    TP6Data data = new TP6Data();
    data.setSector("Energy");
    data.setTargetPeriod("2024-Q1");
    data.setReportVersion("1");

    data.setTargetUnitId("ADS_1-T00001");
    data.setOperatorName("Operator X");
    data.setNumOfFacilities(5);
    data.setTargetType("Absolute");
    data.setMeasurementUnit("kWh");
    data.setThroughputUnit("tonnes");
    data.setBaselineDate(LocalDate.of(2024, 1, 1));
    data.setBaselineEnergy(BigDecimal.valueOf(100000.0));
    data.setBaselineThroughput(BigDecimal.valueOf(2000.0));
    data.setImprovement(BigDecimal.valueOf(15.0));
    data.setBankedSurplusFromPreviousTP(300);

    data.setPreviousBuyOutAfterSurplus(BigDecimal.valueOf(100));
    data.setPreviousSurplusUsed(BigDecimal.valueOf(50));
    data.setSurplusGainedInTP(BigDecimal.valueOf(75));
    return data;
  }

  private Map<TP6ExtractExcelCellsReferenceEnum, Object> createExpectedValuesMap(TP6Data tp6Data) {
    Map<TP6ExtractExcelCellsReferenceEnum, Object> values = new EnumMap<>(TP6ExtractExcelCellsReferenceEnum.class);

    values.put(TP6ExtractExcelCellsReferenceEnum.SECTOR, tp6Data.getSector());
    values.put(TP6ExtractExcelCellsReferenceEnum.TARGET_PERIOD, tp6Data.getTargetPeriod());
    values.put(TP6ExtractExcelCellsReferenceEnum.REPORT_VERSION, tp6Data.getReportVersion());

    values.put(TP6ExtractExcelCellsReferenceEnum.TU_IDENTIFIER, tp6Data.getTargetUnitId());
    values.put(TP6ExtractExcelCellsReferenceEnum.OPERATOR_NAME, tp6Data.getOperatorName());
    values.put(TP6ExtractExcelCellsReferenceEnum.NUM_OF_FACILITIES, tp6Data.getNumOfFacilities());
    values.put(TP6ExtractExcelCellsReferenceEnum.TARGET_TYPE, tp6Data.getTargetType());
    values.put(TP6ExtractExcelCellsReferenceEnum.ENERGY_CARBON_UNIT, tp6Data.getMeasurementUnit());
    values.put(TP6ExtractExcelCellsReferenceEnum.THROUGHPUT_UNIT, tp6Data.getThroughputUnit());
    values.put(TP6ExtractExcelCellsReferenceEnum.BASE_YEAR_START_DATE, tp6Data.getBaselineDate());
    values.put(TP6ExtractExcelCellsReferenceEnum.BASE_YEAR_ENERGY, tp6Data.getBaselineEnergy());
    values.put(TP6ExtractExcelCellsReferenceEnum.BASE_YEAR_THROUGHPUT, tp6Data.getBaselineThroughput());
    values.put(TP6ExtractExcelCellsReferenceEnum.PERCENT_IMPROVEMENT_TARGET, tp6Data.getImprovement());
    values.put(TP6ExtractExcelCellsReferenceEnum.BANKED_SURPLUS_FROM_PREVIOUS_TP, tp6Data.getBankedSurplusFromPreviousTP());

    values.put(TP6ExtractExcelCellsReferenceEnum.PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS, tp6Data.getPreviousBuyOutAfterSurplus());
    values.put(TP6ExtractExcelCellsReferenceEnum.PREVIOUS_SURPLUS_USED, tp6Data.getPreviousSurplusUsed());
    values.put(TP6ExtractExcelCellsReferenceEnum.SURPLUS_GAINED_IN_TP, tp6Data.getSurplusGainedInTP());

    return values;
  }
}

