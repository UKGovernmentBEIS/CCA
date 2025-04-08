package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PrimaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataPrimaryDeterminationContextValidatorTest {

    @InjectMocks
    private TP6PerformanceDataPrimaryDeterminationContextValidator validator;

    @Test
    void validate_no_data_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .primaryDetermination(null)
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_empty_data_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder().build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder().build();

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
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .surplusUsed(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(2030))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .priBuyOutCost(BigDecimal.ZERO)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .surplusUsed(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(2030))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .priBuyOutCost(BigDecimal.ZERO)
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
                        .tpCarbonFactor(BigDecimal.valueOf(0.0183261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8644248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3186895))
                        .co2Emissions(BigDecimal.valueOf(21545.7350252))
                        .surplusUsed(BigDecimal.ONE)
                        .surplusGained(BigDecimal.valueOf(2031))
                        .priBuyOutCarbon(BigDecimal.ONE)
                        .priBuyOutCost(BigDecimal.ONE)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .surplusUsed(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(2030))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .priBuyOutCost(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(8);
    }
}
