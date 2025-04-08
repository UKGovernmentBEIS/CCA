package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.service;

import org.apache.commons.lang3.ObjectUtils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.ActualTargetPeriodPerformance;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PerformanceDataTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PrimaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.SecondaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TargetPeriodPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation.TP6PerformanceDataUploadValidationHelper;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.MathContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.BANKED_SURPLUS_USED;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.BASE_YEAR_PERFORMANCE;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.BUY_OUT_COST;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.CHP_DELIVERED_ELECTRICITY;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.NUMERICAL_TARGET;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.SECONDARY_BUY_OUT_COST;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.SECONDARY_BUY_OUT_REQUIRED;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.AMOUNT_CO2_EMITTED_UNDER_TARGET;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.AMOUNT_ENERGY_USED_UNDER_TARGET;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.BUY_OUT_REQUIRED;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.CARBON_DIOXIDE_EMITTED;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.SURPLUS_GAINED;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_CARBON_FACTOR;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_ENERGY;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_IMPROVEMENT_PERCENTAGE;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.TOLERANCE;
import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum.TOTAL_ENERGY;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataSpreadsheetProcessingExtractDataServiceTest {

    private static final String EXPECTED_SECTOR = "CIA";

    @InjectMocks
    private TP6PerformanceDataSpreadsheetProcessingExtractDataService tp6PerformanceDataSpreadsheetProcessingExtractDataService;

    @Test
    void extractData() throws Exception {
        final long accountId = 1L;
        final PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .build();
        final FileDTO file = FileDTO.builder().fileContent(createMockXlsxTemplate()).build();

        // Invoke
        TP6PerformanceData result = tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractData(accountId, metadata, file);

        // Verify
        assertThat(result).isInstanceOf(TP6PerformanceData.class);
        Assertions.assertEquals(PerformanceDataTargetPeriodType.TP6, result.getType());
        Assertions.assertEquals(PerformanceDataSubmissionType.PRIMARY, result.getSubmissionType());
        Assertions.assertEquals(EXPECTED_SECTOR, result.getSector());
        Assertions.assertNotNull(result.getTargetUnitDetails());
        Assertions.assertNotNull(result.getActualTargetPeriodPerformance());
        Assertions.assertNotNull(result.getPerformanceResult());
        Assertions.assertNotNull(result.getPrimaryDetermination());
        Assertions.assertNotNull(result.getSecondaryDetermination());
        Assertions.assertNotNull(result.getActualTargetPeriodPerformance().getEnergyData());
        Assertions.assertNotNull(result.getActualTargetPeriodPerformance().getCarbonFactors());
        Assertions.assertEquals(11, result.getActualTargetPeriodPerformance().getCarbonFactors().size());
        Assertions.assertNull(result.getTargetUnitDetails().getByEnergyCarbon());
        assertThat(new BigDecimal("322401.659686851", MathContext.DECIMAL128))
                .isEqualByComparingTo(result.getActualTargetPeriodPerformance().getTpEnergy());
        assertThat(new BigDecimal("0.03159102221232158796", MathContext.DECIMAL128))
                .isEqualByComparingTo(result.getPrimaryDetermination().getTpCarbonFactor());
        assertThat(new BigDecimal("1.03159102221232558796", MathContext.DECIMAL128))
                .isEqualByComparingTo(result.getPrimaryDetermination().getCo2Emissions());
        assertThat(new BigDecimal("0.00000000000123456789", MathContext.DECIMAL128))
                .isEqualByComparingTo(result.getPrimaryDetermination().getSurplusUsed());
        assertThat(new BigDecimal("3.0")).isEqualByComparingTo(result.getPrimaryDetermination().getSurplusGained());
    }

    @Test
    void extractCalculatedData_no_data() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetUnitDetails(null)
                .actualTargetPeriodPerformance(null)
                .performanceResult(null)
                .primaryDetermination(null)
                .secondaryDetermination(null)
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder().build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void extractCalculatedData_empty_values() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder().build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder().build())
                .performanceResult(TargetPeriodPerformanceResult.builder().build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpEnergy(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void extractCalculatedData_ABSOLUTE_KWH() {
        // BPC1-T00070_TPR_TP6_V1.xlsx
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(1)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .throughputUnit("tonne")
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.valueOf(1747538.4))
                        .byThroughput(BigDecimal.valueOf(2156.198))
                        .percentTarget(BigDecimal.valueOf(0.02328))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.valueOf(2553.5))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(664403.48),
                                FixedConversionFactor.FUEL_OIL, BigDecimal.valueOf(585548.5),
                                FixedConversionFactor.COAL, BigDecimal.valueOf(954),
                                FixedConversionFactor.LPG, BigDecimal.valueOf(653949.12),
                                FixedConversionFactor.GAS_DIESEL_OIL, BigDecimal.valueOf(3206.5)
                        ))
                        .reportingThroughput(BigDecimal.valueOf(2553.5))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder().build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(new BigDecimal("810.4721366034102619518244613899095", MathContext.DECIMAL128))
                .numericalTarget(new BigDecimal("1706855.706048", MathContext.DECIMAL128))
                .tolerance(BigDecimal.ZERO)
                .tpEnergy(new BigDecimal("1908061.6", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(new BigDecimal("1908061.6", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("-0.091856751187842281462885164640731", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .tpCarbonFactor(new BigDecimal("0.00006169266444437642893709511265254749", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("-201205.893952", MathContext.DECIMAL128))
                .carbonUnderTarget(new BigDecimal("-45.51406823264225746611395215612185", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("431.6158147693333333333333333333333", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.valueOf(46))
                .priBuyOutCost(BigDecimal.valueOf(1150))
                .secondaryBuyOutCo2(BigDecimal.valueOf(46))
                .secondaryBuyOutCost(BigDecimal.valueOf(1150))
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(new BigDecimal("810.4721366034103", MathContext.DECIMAL128))
                .numericalTarget(new BigDecimal("1706855.706048", MathContext.DECIMAL128))
                .tolerance(new BigDecimal("-0.00000000000022737367", MathContext.DECIMAL128)) // Not valid
                .tpEnergy(new BigDecimal("1908061.6", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(new BigDecimal("1908061.6", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("-0.09185675118784231", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .tpCarbonFactor(new BigDecimal("0.00006169266444437642", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("-201205.8939520002", MathContext.DECIMAL128)) // Not valid
                .carbonUnderTarget(new BigDecimal("-45.51406823264217", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("431.615814769333", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.valueOf(46))
                .priBuyOutCost(BigDecimal.valueOf(1150))
                .secondaryBuyOutCo2(BigDecimal.valueOf(46))
                .secondaryBuyOutCost(BigDecimal.valueOf(1150))
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void extractCalculatedData_ABSOLUTE_zero_values() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(1)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .throughputUnit("tonne")
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.ZERO)
                        .byThroughput(BigDecimal.valueOf(21117))
                        .percentTarget(BigDecimal.ZERO)
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.valueOf(19947))
                        .energyData(Map.of())
                        .reportingThroughput(BigDecimal.ZERO)
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder().build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(BigDecimal.ZERO)
                .numericalTarget(BigDecimal.ZERO)
                .tolerance(BigDecimal.ZERO)
                .tpEnergy(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .tpPerformancePercent(BigDecimal.ZERO)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(BigDecimal.ZERO)
                .energyCarbonUnderTarget(BigDecimal.ZERO)
                .carbonUnderTarget(BigDecimal.ZERO)
                .co2Emissions(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(BigDecimal.ZERO)
                .numericalTarget(BigDecimal.ZERO)
                .tolerance(BigDecimal.ZERO)
                .tpEnergy(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .tpPerformancePercent(BigDecimal.ZERO)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(BigDecimal.ZERO)
                .energyCarbonUnderTarget(BigDecimal.ZERO)
                .carbonUnderTarget(BigDecimal.ZERO)
                .co2Emissions(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void extractCalculatedData_RELATIVE_KWH() {
        // AIC-T00060_TPR_TP6_V1.xlsx
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.RELATIVE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(1)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .throughputUnit("tonne")
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.valueOf(524660.807))
                        .byThroughput(BigDecimal.valueOf(28423.684))
                        .percentTarget(BigDecimal.valueOf(0.02582))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.valueOf(1440560.5))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(586311.7)
                        ))
                        .reportingThroughput(BigDecimal.valueOf(30592.815933574))
                        .adjustedThroughput(BigDecimal.valueOf(30592.815933574))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder().build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(new BigDecimal("18.45857866277995491365580900772750", MathContext.DECIMAL128))
                .numericalTarget(new BigDecimal("17.98197816170697647778521601914798", MathContext.DECIMAL128))
                .tolerance(BigDecimal.ZERO)
                .tpEnergy(new BigDecimal("586311.7", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(new BigDecimal("10393095.50000003672757690693351091", MathContext.DECIMAL128))
                .tpPerformance(new BigDecimal("19.16501250728455728981876014726794", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("-0.038271302325626077279496228308392", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .tpCarbonFactor(new BigDecimal("0.0000546", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("-36192.35197735110406833641831931416", MathContext.DECIMAL128))
                .carbonUnderTarget(new BigDecimal("-7.245708865865691034480950947526694", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("117.37960234", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.valueOf(8))
                .priBuyOutCost(BigDecimal.valueOf(200))
                .secondaryBuyOutCo2(BigDecimal.valueOf(8))
                .secondaryBuyOutCost(BigDecimal.valueOf(200))
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(new BigDecimal("18.458578662779956", MathContext.DECIMAL128))
                .numericalTarget(new BigDecimal("17.98197816170702", MathContext.DECIMAL128))
                .tolerance(new BigDecimal("0.00000000000004263257", MathContext.DECIMAL128))
                .tpEnergy(new BigDecimal("586311.7", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(new BigDecimal("10393095.50000004", MathContext.DECIMAL128)) // Not valid
                .tpPerformance(new BigDecimal("19.16501250728456", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("-0.03827130232562581", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .tpCarbonFactor(new BigDecimal("0.0000546", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("-36192.35197734863", MathContext.DECIMAL128)) // Not valid
                .carbonUnderTarget(new BigDecimal("-7.245708865865175", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("117.37960234", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.valueOf(8))
                .priBuyOutCost(BigDecimal.valueOf(200))
                .secondaryBuyOutCo2(BigDecimal.valueOf(8))
                .secondaryBuyOutCost(BigDecimal.valueOf(200))
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void extractCalculatedData_RELATIVE_zero_values() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.RELATIVE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(1)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .throughputUnit("tonne")
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.ZERO)
                        .byThroughput(BigDecimal.ZERO)
                        .percentTarget(BigDecimal.ZERO)
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.valueOf(1440560.5))
                        .energyData(Map.of())
                        .reportingThroughput(BigDecimal.valueOf(30592.815933574))
                        .adjustedThroughput(BigDecimal.valueOf(30592.815933574))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder().build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(BigDecimal.ZERO)
                .numericalTarget(BigDecimal.ZERO)
                .tolerance(BigDecimal.ZERO)
                .tpEnergy(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .tpPerformancePercent(BigDecimal.ZERO)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(BigDecimal.ZERO)
                .energyCarbonUnderTarget(BigDecimal.ZERO)
                .carbonUnderTarget(BigDecimal.ZERO)
                .co2Emissions(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(BigDecimal.ZERO)
                .numericalTarget(BigDecimal.ZERO)
                .tolerance(BigDecimal.ZERO)
                .tpEnergy(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .tpPerformancePercent(BigDecimal.ZERO)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(BigDecimal.ZERO)
                .energyCarbonUnderTarget(BigDecimal.ZERO)
                .carbonUnderTarget(BigDecimal.ZERO)
                .co2Emissions(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void extractCalculatedData_NOVEM_KG() {
        // CONF-T00008_TPR_TP6_V1.xlsx
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.NOVEM)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(4)
                        .energyCarbonUnit(MeasurementType.CARBON_KG)
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.valueOf(7840947.75))
                        .percentTarget(BigDecimal.valueOf(0.03786))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.valueOf(940282.500))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(94808719.2),
                                FixedConversionFactor.NATURAL_GAS, BigDecimal.valueOf(92614),
                                FixedConversionFactor.LPG, BigDecimal.valueOf(772731.000),
                                FixedConversionFactor.GAS_DIESEL_OIL, BigDecimal.valueOf(17681733)
                        ))
                        .carbonFactors(List.of(
                                OtherFuel.builder().conversionFactor(BigDecimal.ZERO).consumption(BigDecimal.valueOf(51166745)).build()
                        ))
                        .reportingThroughput(BigDecimal.valueOf(940282.5))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .targetEnergyCarbonTpThroughput(BigDecimal.valueOf(6705429.325))
                        .byEnergyCarbonTpThroughput(BigDecimal.valueOf(6959296.8792445))
                        .build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(null)
                .numericalTarget(null)
                .tolerance(null)
                .tpEnergy(new BigDecimal("164522542.2", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(new BigDecimal("6566713.200219999999999999999999999", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("0.0564113998635906466167229718067345", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(new BigDecimal("0.0399137596125718023338445593262903", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("138716.124780000000000000000000001", MathContext.DECIMAL128))
                .carbonUnderTarget(new BigDecimal("508.6257908600000000000000000000037", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("24077.94840080666666666666666666667", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(508))
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(null)
                .numericalTarget(null)
                .tolerance(null)
                .tpEnergy(new BigDecimal("164522542.2", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(new BigDecimal("6566713.20022", MathContext.DECIMAL128)) // Not valid
                .tpPerformancePercent(new BigDecimal("0.05641139986359067", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(new BigDecimal("0.0399137596125718", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("138716.1247800002", MathContext.DECIMAL128)) // Not valid
                .carbonUnderTarget(new BigDecimal("508.62579086", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("24077.9484008067", MathContext.DECIMAL128)) // Not valid
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(508))
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void extractCalculatedData_NOVEM_TONNE() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.NOVEM)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(4)
                        .energyCarbonUnit(MeasurementType.CARBON_TONNE)
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.valueOf(7840947.750))
                        .percentTarget(BigDecimal.valueOf(0.03786))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.valueOf(940282.500))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(94808719.200),
                                FixedConversionFactor.NATURAL_GAS, BigDecimal.valueOf(92614.000),
                                FixedConversionFactor.LPG, BigDecimal.valueOf(772731.000),
                                FixedConversionFactor.GAS_DIESEL_OIL, BigDecimal.valueOf(17681733)
                        ))
                        .carbonFactors(List.of(
                                OtherFuel.builder().conversionFactor(BigDecimal.ZERO).consumption(BigDecimal.valueOf(51166745)).build()
                        ))
                        .reportingThroughput(BigDecimal.valueOf(940282.5))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .targetEnergyCarbonTpThroughput(BigDecimal.valueOf(6705429.325))
                        .byEnergyCarbonTpThroughput(BigDecimal.valueOf(6959296.8792445))
                        .build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(null)
                .numericalTarget(null)
                .tolerance(null)
                .tpEnergy(new BigDecimal("164522542.2", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(new BigDecimal("6566.713200219999999999999999999999", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("0.9990564113998635906466167229718067", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(new BigDecimal("0.03991375961257180233384455932629030", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("6698862.61179978", MathContext.DECIMAL128))
                .carbonUnderTarget(new BigDecimal("24562496.24326586", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("24077.94840080666666666666666666667", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(24562496))
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(null)
                .numericalTarget(null)
                .tolerance(null)
                .tpEnergy(new BigDecimal("164522542.2", MathContext.DECIMAL128))
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(new BigDecimal("6566.71320022", MathContext.DECIMAL128))
                .tpPerformancePercent(new BigDecimal("0.999056411399864", MathContext.DECIMAL128))
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(new BigDecimal("0.0399137596125718", MathContext.DECIMAL128))
                .energyCarbonUnderTarget(new BigDecimal("6698862.61179978", MathContext.DECIMAL128))
                .carbonUnderTarget(new BigDecimal("24562496.2432659", MathContext.DECIMAL128))
                .co2Emissions(new BigDecimal("24077.9484008067", MathContext.DECIMAL128))
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(24562496))
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void extractCalculatedData_NOVEM_zero_values() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.NOVEM)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(1)
                        .energyCarbonUnit(MeasurementType.ENERGY_GJ)
                        .byStartDate(LocalDate.of(2024, 9, 2))
                        .byEnergyCarbon(BigDecimal.ZERO)
                        .percentTarget(BigDecimal.ONE)
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualThroughput(BigDecimal.ZERO)
                        .energyData(Map.of())
                        .reportingThroughput(BigDecimal.ZERO)
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .targetEnergyCarbonTpThroughput(BigDecimal.ZERO)
                        .byEnergyCarbonTpThroughput(BigDecimal.ZERO)
                        .build())
                .primaryDetermination(PrimaryDetermination.builder().build())
                .secondaryDetermination(SecondaryDetermination.builder().build())
                .build();

        PerformanceDataCalculatedMetrics expected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(null)
                .numericalTarget(null)
                .tolerance(null)
                .tpEnergy(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .tpPerformancePercent(BigDecimal.ONE)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(BigDecimal.ZERO)
                .energyCarbonUnderTarget(BigDecimal.ZERO)
                .carbonUnderTarget(BigDecimal.ZERO)
                .co2Emissions(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();
        PerformanceDataCalculatedMetrics excelExpected = PerformanceDataCalculatedMetrics.builder()
                .byPerformance(null)
                .numericalTarget(null)
                .tolerance(null)
                .tpEnergy(BigDecimal.ZERO)
                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                .tpPerformance(BigDecimal.ZERO)
                .tpPerformancePercent(BigDecimal.ONE)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .tpCarbonFactor(BigDecimal.ZERO)
                .energyCarbonUnderTarget(BigDecimal.ZERO)
                .carbonUnderTarget(BigDecimal.ZERO)
                .co2Emissions(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ZERO)
                .secondaryBuyOutCost(BigDecimal.ZERO)
                .build();

        // Invoke
        PerformanceDataCalculatedMetrics actual = tp6PerformanceDataSpreadsheetProcessingExtractDataService
                .extractCalculatedData(performanceData);

        // Verify
        calculatedEquals(actual, expected, excelExpected);
    }

    @Test
    void testGetDocumentTemplateType() {
        assertThat(tp6PerformanceDataSpreadsheetProcessingExtractDataService.getDocumentTemplateType())
                .isEqualTo(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
    }

    private void calculatedEquals(PerformanceDataCalculatedMetrics actual, PerformanceDataCalculatedMetrics expected, PerformanceDataCalculatedMetrics excel) {
        assertAll(
                () -> {
                    if(ObjectUtils.isEmpty(actual.getByPerformance())) {
                        assertThat(actual.getByPerformance()).isEqualTo(expected.getByPerformance());
                    } else {
                        assertThat(actual.getByPerformance()).isEqualByComparingTo(expected.getByPerformance());
                    }
                },
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(BASE_YEAR_PERFORMANCE)
                        .process(actual.getByPerformance(), excel.getByPerformance()).isValid())
                        .withFailMessage("byPerformance not valid").isTrue(),

                () -> {
                    if(ObjectUtils.isEmpty(actual.getNumericalTarget())) {
                        assertThat(actual.getNumericalTarget()).isEqualTo(expected.getNumericalTarget());
                    } else {
                        assertThat(actual.getNumericalTarget()).isEqualByComparingTo(expected.getNumericalTarget());
                    }
                },
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(NUMERICAL_TARGET)
                        .process(actual.getNumericalTarget(), excel.getNumericalTarget()).isValid())
                        .withFailMessage("numericalTarget not valid").isTrue(),

                () -> {
                    if(ObjectUtils.isEmpty(actual.getTolerance())) {
                        assertThat(actual.getTolerance()).isEqualTo(expected.getTolerance());
                    } else {
                        assertThat(actual.getTolerance()).isEqualByComparingTo(expected.getTolerance());
                    }
                },
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(TOLERANCE)
                        .process(actual.getTolerance(), excel.getTolerance()).isValid())
                        .withFailMessage("tolerance not valid").isTrue(),

                () -> assertThat(actual.getTpEnergy()).isEqualByComparingTo(expected.getTpEnergy()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(TOTAL_ENERGY)
                        .process(actual.getTpEnergy(), excel.getTpEnergy()).isValid())
                        .withFailMessage("tpEnergy not valid").isTrue(),

                () -> assertThat(actual.getTpChpDeliveredElectricity()).isEqualByComparingTo(expected.getTpChpDeliveredElectricity()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(CHP_DELIVERED_ELECTRICITY)
                        .process(actual.getTpChpDeliveredElectricity(), excel.getTpChpDeliveredElectricity()).isValid())
                        .withFailMessage("tpChpDeliveredElectricity not valid").isTrue(),

                () -> assertThat(actual.getTpPerformance()).isEqualByComparingTo(expected.getTpPerformance()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(TARGET_PERIOD_ENERGY)
                        .process(actual.getTpPerformance(), excel.getTpPerformance()).isValid())
                        .withFailMessage("tpPerformance not valid").isTrue(),

                () -> assertThat(actual.getTpPerformancePercent()).isEqualByComparingTo(expected.getTpPerformancePercent()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(TARGET_PERIOD_IMPROVEMENT_PERCENTAGE)
                        .process(actual.getTpPerformancePercent(), excel.getTpPerformancePercent()).isValid())
                        .withFailMessage("tpPerformancePercent not valid").isTrue(),

                () -> assertThat(actual.getTpOutcome()).isEqualTo(expected.getTpOutcome()),

                () -> assertThat(actual.getTpCarbonFactor()).isEqualByComparingTo(expected.getTpCarbonFactor()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(TARGET_PERIOD_CARBON_FACTOR)
                        .process(actual.getTpCarbonFactor(), excel.getTpCarbonFactor()).isValid())
                        .withFailMessage("tpCarbonFactor not valid").isTrue(),

                () -> assertThat(actual.getEnergyCarbonUnderTarget()).isEqualByComparingTo(expected.getEnergyCarbonUnderTarget()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(AMOUNT_ENERGY_USED_UNDER_TARGET)
                        .process(actual.getEnergyCarbonUnderTarget(), excel.getEnergyCarbonUnderTarget()).isValid())
                        .withFailMessage("energyCarbonUnderTarget not valid").isTrue(),

                () -> assertThat(actual.getCarbonUnderTarget()).isEqualByComparingTo(expected.getCarbonUnderTarget()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(AMOUNT_CO2_EMITTED_UNDER_TARGET)
                        .process(actual.getCarbonUnderTarget(), excel.getCarbonUnderTarget()).isValid())
                        .withFailMessage("carbonUnderTarget not valid").isTrue(),

                () -> assertThat(actual.getCo2Emissions()).isEqualByComparingTo(expected.getCo2Emissions()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(CARBON_DIOXIDE_EMITTED)
                        .process(actual.getCo2Emissions(), excel.getCo2Emissions()).isValid())
                        .withFailMessage("co2Emissions not valid").isTrue(),

                () -> assertThat(actual.getSurplusUsed()).isEqualByComparingTo(expected.getSurplusUsed()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(BANKED_SURPLUS_USED)
                        .process(actual.getSurplusUsed(), excel.getSurplusUsed()).isValid())
                        .withFailMessage("surplusUsed not valid").isTrue(),

                () -> assertThat(actual.getSurplusGained()).isEqualByComparingTo(expected.getSurplusGained()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(SURPLUS_GAINED)
                        .process(actual.getSurplusGained(), excel.getSurplusGained()).isValid())
                        .withFailMessage("surplusGained not valid").isTrue(),

                () -> assertThat(actual.getPriBuyOutCarbon()).isEqualByComparingTo(expected.getPriBuyOutCarbon()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(BUY_OUT_REQUIRED)
                        .process(actual.getPriBuyOutCarbon(), excel.getPriBuyOutCarbon()).isValid())
                        .withFailMessage("priBuyOutCarbon not valid").isTrue(),

                () -> assertThat(actual.getPriBuyOutCost()).isEqualByComparingTo(expected.getPriBuyOutCost()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(BUY_OUT_COST)
                        .process(actual.getPriBuyOutCost(), excel.getPriBuyOutCost()).isValid())
                        .withFailMessage("priBuyOutCost not valid").isTrue(),

                () -> assertThat(actual.getSecondaryBuyOutCo2()).isEqualByComparingTo(expected.getSecondaryBuyOutCo2()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(SECONDARY_BUY_OUT_REQUIRED)
                        .process(actual.getSecondaryBuyOutCo2(), excel.getSecondaryBuyOutCo2()).isValid())
                        .withFailMessage("secondaryBuyOutCo2 not valid").isTrue(),

                () -> assertThat(actual.getSecondaryBuyOutCost()).isEqualByComparingTo(expected.getSecondaryBuyOutCost()),
                () -> assertThat(TP6PerformanceDataUploadValidationHelper.validateCalculateValueEquals(SECONDARY_BUY_OUT_COST)
                        .process(actual.getSecondaryBuyOutCost(), excel.getSecondaryBuyOutCost()).isValid())
                        .withFailMessage("secondaryBuyOutCost not valid").isTrue()
        );
    }

    private static byte[] createMockXlsxTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          workbook.createSheet("Sheet0");
          Sheet sheet = workbook.createSheet("Sheet1");
          sheet.createRow(4).createCell(2).setCellValue(EXPECTED_SECTOR);
          sheet.createRow(22).createCell(4).setCellValue("");
          sheet.createRow(62).createCell(5).setCellValue("322401.65968685100000");
          sheet.createRow(112).createCell(4).setCellValue("0.03159102221232158796000000");
          sheet.createRow(115).createCell(4).setCellValue("1.03159102221232558796000000");
          sheet.createRow(116).createCell(4).setCellValue("0.00000000000123456789");
          sheet.createRow(38).createCell(5).setCellValue("2");
          sheet.createRow(39).createCell(5).setCellValue("1");
          Cell formulaCell = sheet.createRow(117).createCell(4);
          formulaCell.setCellFormula("F40+F39");
          FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
          evaluator.evaluateFormulaCell(formulaCell);

          workbook.write(out);
          return out.toByteArray();
        }
      }
}
