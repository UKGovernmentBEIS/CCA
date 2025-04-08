package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.SurplusBuyOutDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PrimaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.SecondaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataSecondaryDeterminationContextValidatorTest {

    @InjectMocks
    private TP6PerformanceDataSecondaryDeterminationContextValidator validator;

    @Test
    void validate_no_data_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .secondaryDetermination(null)
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
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder().submissionType(PerformanceDataSubmissionType.PRIMARY).build();

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
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_not_equal_with_primary_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0192261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(40380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(3030.3185895))
                        .co2Emissions(BigDecimal.valueOf(31545.7360252))
                        .priBuyOutCarbon(BigDecimal.TEN)
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(5);
    }

    @Test
    void validate_with_primary_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .prevBuyOutCo2(BigDecimal.ONE)
                        .prevSurplusUsed(BigDecimal.ONE)
                        .prevSurplusGained(BigDecimal.ONE)
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(3);
    }

    @Test
    void validate_calculations_not_equal_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .secondaryBuyOutCo2(BigDecimal.ONE)
                        .secondaryBuyOutCost(BigDecimal.valueOf(300))
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .primaryDetermination(PrimaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .build())
                .secondaryDetermination(SecondaryDetermination.builder()
                        .tpCarbonFactor(BigDecimal.valueOf(0.0182261))
                        .energyCarbonUnderTarget(BigDecimal.valueOf(30380.8643248))
                        .carbonUnderTarget(BigDecimal.valueOf(2030.3185895))
                        .co2Emissions(BigDecimal.valueOf(21545.7360252))
                        .priBuyOutCarbon(BigDecimal.ZERO)
                        .secondaryBuyOutCo2(BigDecimal.TWO)
                        .secondaryBuyOutCost(BigDecimal.valueOf(200))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_no_data_Secondary_without_lastReport_VALID() {
        final PerformanceDataCalculatedMetrics calculatedData = PerformanceDataCalculatedMetrics.builder()
                .secondaryBuyOutCo2(BigDecimal.ONE)
                .secondaryBuyOutCost(BigDecimal.TWO)
                .build();
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .lastUploadedReport(null)
                .performanceDataCalculatedMetrics(calculatedData)
                .build();
        final SecondaryDetermination secondaryDetermination = SecondaryDetermination.builder()
                .prevBuyOutCo2(BigDecimal.ZERO)
                .prevSurplusGained(BigDecimal.ZERO)
                .prevSurplusUsed(BigDecimal.ZERO)
                .secondaryBuyOutCo2(BigDecimal.ONE)
                .secondaryBuyOutCost(BigDecimal.TWO)
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .secondaryDetermination(secondaryDetermination)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_no_data_Secondary_with_lastReport_VALID() {
        final PerformanceDataCalculatedMetrics calculatedData = PerformanceDataCalculatedMetrics.builder()
                .secondaryBuyOutCo2(BigDecimal.ONE)
                .secondaryBuyOutCost(BigDecimal.TWO)
                .build();

        final PerformanceDataContainer lastUploadedReport = PerformanceDataContainer.builder()
                .surplusBuyOutDetermination(
                        SurplusBuyOutDetermination.builder()
                                .priBuyOutCarbon(BigDecimal.valueOf(1.234))
                                .surplusGained(BigDecimal.valueOf(2.111))
                                .surplusUsed(BigDecimal.valueOf(3.2222))
                                .build()
                )
                .build();
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .lastUploadedReport(lastUploadedReport)
                .performanceDataCalculatedMetrics(calculatedData)
                .build();
        final SecondaryDetermination section = SecondaryDetermination.builder()
                .prevBuyOutCo2(BigDecimal.valueOf(1.234))
                .prevSurplusGained(BigDecimal.valueOf(2.111))
                .prevSurplusUsed(BigDecimal.valueOf(3.2222))
                .secondaryBuyOutCo2(BigDecimal.ONE)
                .secondaryBuyOutCost(BigDecimal.TWO)
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .secondaryDetermination(section)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }
}
