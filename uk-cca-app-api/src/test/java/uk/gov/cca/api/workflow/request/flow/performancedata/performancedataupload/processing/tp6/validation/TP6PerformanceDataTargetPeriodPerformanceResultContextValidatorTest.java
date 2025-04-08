package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TargetPeriodPerformanceResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataTargetPeriodPerformanceResultContextValidatorTest {

    @InjectMocks
    private TP6PerformanceDataTargetPeriodPerformanceResultContextValidator validator;

    @Test
    void validate_no_data_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .performanceResult(null)
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpPerformance(BigDecimal.valueOf(1908061.6))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0918567511878423))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetType(AgreementCompositionType.ABSOLUTE)
                .type(PerformanceDataTargetPeriodType.TP6)
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .tpPerformance(BigDecimal.valueOf(1908061.6))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0918567511878423))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_with_NOVEM_zero_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpPerformance(BigDecimal.valueOf(1908061.6))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0918567511878423))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetType(AgreementCompositionType.ABSOLUTE)
                .type(PerformanceDataTargetPeriodType.TP6)
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .targetEnergyCarbonTpThroughput(BigDecimal.ZERO)
                        .byEnergyCarbonTpThroughput(BigDecimal.ZERO)
                        .tpPerformance(BigDecimal.valueOf(1908061.6))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0918567511878423))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_with_novem_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpPerformance(BigDecimal.valueOf(19.1650125))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0382713))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetType(AgreementCompositionType.RELATIVE)
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .targetEnergyCarbonTpThroughput(BigDecimal.TEN)
                        .byEnergyCarbonTpThroughput(BigDecimal.ONE)
                        .tpPerformance(BigDecimal.valueOf(19.1650125))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0382713))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_NOVEM_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpPerformance(BigDecimal.valueOf(32012.61882))
                        .tpPerformancePercent(BigDecimal.valueOf(-4.2910388))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetType(AgreementCompositionType.NOVEM)
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .targetEnergyCarbonTpThroughput(BigDecimal.TEN)
                        .byEnergyCarbonTpThroughput(BigDecimal.valueOf(6050.3466889))
                        .tpPerformance(BigDecimal.valueOf(32012.61882))
                        .tpPerformancePercent(BigDecimal.valueOf(-4.2910388))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_no_novem_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpPerformance(BigDecimal.valueOf(32012.61882))
                        .tpPerformancePercent(BigDecimal.valueOf(-4.2910388))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetType(AgreementCompositionType.NOVEM)
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .tpPerformance(BigDecimal.valueOf(32012.61882))
                        .tpPerformancePercent(BigDecimal.valueOf(-4.2910388))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_calculations_not_equal_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .tpPerformance(BigDecimal.valueOf(1908061.7))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0916567511878423))
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetType(AgreementCompositionType.ABSOLUTE)
                .type(PerformanceDataTargetPeriodType.TP6)
                .performanceResult(TargetPeriodPerformanceResult.builder()
                        .tpPerformance(BigDecimal.valueOf(1908061.6))
                        .tpPerformancePercent(BigDecimal.valueOf(-0.0918567511878423))
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(3);
    }
}
