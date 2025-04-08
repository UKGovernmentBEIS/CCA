package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.ActualTargetPeriodPerformance;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataActualTargetPeriodPerformanceContextValidatorTest {

    @InjectMocks
    private TP6PerformanceDataActualTargetPeriodPerformanceContextValidator validator;

    @Test
    void validate_no_data_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .actualTargetPeriodPerformance(null)
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpEnergy(BigDecimal.valueOf(40.4000))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(7.75716226151309))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(300.1555789))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(10.10),
                                FixedConversionFactor.COAL, BigDecimal.valueOf(10.10)
                        ))
                        .carbonFactors(List.of(
                                new OtherFuel("Other Fuel 1", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10)),
                                new OtherFuel("Other Fuel 2", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10))
                        ))
                        .tpEnergy(BigDecimal.valueOf(40.4000))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(7.75716226151309))
                        .reportingThroughput(BigDecimal.valueOf(100.1555789))
                        .adjustedThroughput(BigDecimal.valueOf(100.1555789))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_calculations_not_equal_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpEnergy(BigDecimal.valueOf(40.4100))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(7.75716236151309))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(300.1555789))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(10.10),
                                FixedConversionFactor.COAL, BigDecimal.valueOf(10.10)
                        ))
                        .carbonFactors(List.of(
                                new OtherFuel("Other Fuel 1", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10)),
                                new OtherFuel("Other Fuel 2", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10)),
                                new OtherFuel(null, BigDecimal.ZERO, BigDecimal.ZERO)
                        ))
                        .tpEnergy(BigDecimal.valueOf(40.4000))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(7.85716226151309))
                        .reportingThroughput(BigDecimal.valueOf(100.1555789))
                        .adjustedThroughput(BigDecimal.valueOf(100.1555789))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_CarbonFactors_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpEnergy(BigDecimal.valueOf(40.4000))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(7.75716226151309))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(300.1555789))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(10.10),
                                FixedConversionFactor.COAL, BigDecimal.valueOf(10.10)
                        ))
                        .carbonFactors(List.of(
                                new OtherFuel("Other Fuel 1", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10)),
                                new OtherFuel(null, BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10)),
                                new OtherFuel("Other Fuel 3", BigDecimal.valueOf(10.10), null)
                        ))
                        .tpEnergy(BigDecimal.valueOf(40.4000))
                        .tpChpDeliveredElectricity(BigDecimal.valueOf(7.75716226151309))
                        .reportingThroughput(BigDecimal.valueOf(100.1555789))
                        .adjustedThroughput(BigDecimal.valueOf(100.1555789))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat((result.getViolations().getFirst().getData()[0]).toString())
                .hasToString("Other fuel identifier must be provided if consumption is greater than 0");
    }

    @Test
    void validate_reportingThroughput_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpEnergy(BigDecimal.valueOf(40.4000))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .actualTargetPeriodPerformance(ActualTargetPeriodPerformance.builder()
                        .actualTuIdentifier("businessId")
                        .actualThroughput(BigDecimal.valueOf(300.1555789))
                        .energyData(Map.of(
                                FixedConversionFactor.ELECTRICITY, BigDecimal.valueOf(10.10),
                                FixedConversionFactor.COAL, BigDecimal.valueOf(10.10)
                        ))
                        .carbonFactors(List.of(
                                new OtherFuel("Other Fuel 1", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10)),
                                new OtherFuel("Other Fuel 2", BigDecimal.valueOf(10.10), BigDecimal.valueOf(10.10))
                        ))
                        .tpEnergy(BigDecimal.valueOf(40.40))
                        .reportingThroughput(BigDecimal.valueOf(100.1555789))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat((result.getViolations().getFirst().getData()[0]).toString())
                .hasToString("E69 Calculated values differ from expected values. Please download the latest version of the reporting spreadsheet for this target unit.");
    }
}
