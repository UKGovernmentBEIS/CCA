package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TP6PerformanceDataTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_ABSOLUTE_valid() {
        TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sectorId")
                .reportVersion(1)
                .templateVersion("1.0")
                .reportDate(LocalDate.now())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(0)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .throughputUnit("unit")
                        .byStartDate(LocalDate.now().minusDays(1))
                        .byEnergyCarbon(BigDecimal.valueOf(100))
                        .byThroughput(BigDecimal.valueOf(100))
                        .byPerformance(BigDecimal.valueOf(100))
                        .numericalTarget(BigDecimal.valueOf(100))
                        .tolerance(BigDecimal.valueOf(100))
                        .percentTarget(BigDecimal.valueOf(100))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(200))
                        .energyData(Map.of(
                                FixedConversionFactor.COAL, BigDecimal.valueOf(100)
                        ))
                        .tpEnergy(BigDecimal.valueOf(100))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(100))
                        .reportingThroughput(BigDecimal.valueOf(200))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .tpPerformance(BigDecimal.valueOf(100))
                        .tpPerformancePercent(BigDecimal.valueOf(100))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(100))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(100))
                        .carbonUnderTarget(BigDecimal.valueOf(100))
                        .co2Emissions(BigDecimal.valueOf(100))
                        .priBuyOutCarbon(BigDecimal.valueOf(100))
                        .surplusUsed(BigDecimal.valueOf(100))
                        .surplusGained(BigDecimal.valueOf(100))
                        .priBuyOutCost(BigDecimal.valueOf(100))
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(100))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(100))
                        .carbonUnderTarget(BigDecimal.valueOf(100))
                        .co2Emissions(BigDecimal.valueOf(100))
                        .priBuyOutCarbon(BigDecimal.valueOf(100))
                        .secondaryBuyOutCo2(BigDecimal.ZERO)
                        .secondaryBuyOutCost(BigDecimal.ZERO)
                        .build())
                .build();

        final Set<ConstraintViolation<TP6PerformanceData>> violations = validator.validate(performanceData);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_NOVEM_valid() {
        TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sectorId")
                .reportVersion(1)
                .templateVersion("1.0")
                .reportDate(LocalDate.now())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .targetType(AgreementCompositionType.NOVEM)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(0)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .byStartDate(LocalDate.now().minusDays(1))
                        .byEnergyCarbon(BigDecimal.valueOf(100))
                        .percentTarget(BigDecimal.valueOf(100))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(200))
                        .energyData(Map.of(
                                FixedConversionFactor.COAL, BigDecimal.valueOf(100)
                        ))
                        .tpEnergy(BigDecimal.valueOf(100))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(100))
                        .reportingThroughput(BigDecimal.valueOf(200))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .tpPerformance(BigDecimal.valueOf(100))
                        .tpPerformancePercent(BigDecimal.valueOf(100))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(100))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(100))
                        .carbonUnderTarget(BigDecimal.valueOf(100))
                        .co2Emissions(BigDecimal.valueOf(100))
                        .priBuyOutCarbon(BigDecimal.valueOf(100))
                        .surplusUsed(BigDecimal.valueOf(100))
                        .surplusGained(BigDecimal.valueOf(100))
                        .priBuyOutCost(BigDecimal.valueOf(100))
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(100))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(100))
                        .carbonUnderTarget(BigDecimal.valueOf(100))
                        .co2Emissions(BigDecimal.valueOf(100))
                        .priBuyOutCarbon(BigDecimal.valueOf(100))
                        .secondaryBuyOutCo2(BigDecimal.ZERO)
                        .secondaryBuyOutCost(BigDecimal.ZERO)
                        .build())
                .build();

        final Set<ConstraintViolation<TP6PerformanceData>> violations = validator.validate(performanceData);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_with_Secondary_valid() {
        TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sectorId")
                .reportVersion(1)
                .templateVersion("1.0")
                .reportDate(LocalDate.now())
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .targetUnitDetails(PerformanceDataTargetUnitDetails.builder()
                        .tuIdentifier("businessId")
                        .operatorName("operatorName")
                        .numOfFacilities(0)
                        .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                        .throughputUnit("unit")
                        .byStartDate(LocalDate.now().minusDays(1))
                        .byEnergyCarbon(BigDecimal.valueOf(100))
                        .byThroughput(BigDecimal.valueOf(100))
                        .byPerformance(BigDecimal.valueOf(100))
                        .numericalTarget(BigDecimal.valueOf(100))
                        .tolerance(BigDecimal.valueOf(100))
                        .percentTarget(BigDecimal.valueOf(100))
                        .bankedSurplus(BigDecimal.ZERO)
                        .build())
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(200))
                        .energyData(Map.of(
                                FixedConversionFactor.COAL, BigDecimal.valueOf(100)
                        ))
                        .tpEnergy(BigDecimal.valueOf(100))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(100))
                        .reportingThroughput(BigDecimal.valueOf(200))
                        .build())
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .tpPerformance(BigDecimal.valueOf(100))
                        .tpPerformancePercent(BigDecimal.valueOf(100))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(100))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(100))
                        .carbonUnderTarget(BigDecimal.valueOf(100))
                        .co2Emissions(BigDecimal.valueOf(100))
                        .priBuyOutCarbon(BigDecimal.valueOf(100))
                        .surplusUsed(BigDecimal.valueOf(100))
                        .surplusGained(BigDecimal.valueOf(100))
                        .priBuyOutCost(BigDecimal.valueOf(100))
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(100))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(100))
                        .carbonUnderTarget(BigDecimal.valueOf(100))
                        .co2Emissions(BigDecimal.valueOf(100))
                        .priBuyOutCarbon(BigDecimal.valueOf(100))
                        .prevBuyOutCo2(BigDecimal.valueOf(100))
                        .prevSurplusUsed(BigDecimal.valueOf(100))
                        .prevSurplusGained(BigDecimal.valueOf(100))
                        .secondaryBuyOutCo2(BigDecimal.valueOf(100))
                        .secondaryBuyOutCost(BigDecimal.valueOf(100))
                        .build())
                .build();

        final Set<ConstraintViolation<TP6PerformanceData>> violations = validator.validate(performanceData);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_sections_not_null_base_sections() {
        TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sectorId")
                .reportVersion(1)
                .templateVersion("1.0")
                .reportDate(LocalDate.now())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .targetUnitDetails(null)
                .actualTargetPeriodPerformance(null)
                .performanceResult(null)
                .primaryDetermination(null)
                .secondaryDetermination(null)
                .build();

        final Set<ConstraintViolation<TP6PerformanceData>> violations = validator.validate(performanceData);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "{performanceData.primaryDetermination.notEmpty}",
                        "{performanceData.secondaryDetermination.notEmpty}",
                        "{performanceData.targetUnitDetails.notEmpty}",
                        "{performanceData.performanceResult.notEmpty}",
                        "{performanceData.actualTargetPeriodPerformance.notEmpty}"
                );
    }

    @Test
    void validate_all_null_not_valid() {
        TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetUnitDetails(null)
                .actualTargetPeriodPerformance(null)
                .performanceResult(null)
                .primaryDetermination(null)
                .secondaryDetermination(null)
                .build();

        final Set<ConstraintViolation<TP6PerformanceData>> violations = validator.validate(performanceData);

        assertThat(violations).hasSize(10);
    }
}
