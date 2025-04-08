package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PerformanceDataTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataTargetUnitDetailsContextValidatorTest {

    @InjectMocks
    private TP6PerformanceDataTargetUnitDetailsContextValidator validator;

    @Test
    void validate_no_data_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetUnitDetails(null)
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
                        .name("operatorName")
                        .build())
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder().build()))
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .measurementType(MeasurementType.ENERGY_KWH)
                                                        .throughputUnit("tonne")
                                                        .build())
                                                .baselineData(BaselineData.builder()
                                                        .baselineDate(LocalDate.of(2024, 9, 2))
                                                        .energy(BigDecimal.valueOf(524660.807))
                                                        .throughput(BigDecimal.valueOf(28423.684))
                                                        .build())
                                                .targets(Targets.builder()
                                                        .improvement(BigDecimal.valueOf(2.582))
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();
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
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
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
                        .name("operatorName")
                        .build())
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder().build()))
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .measurementType(MeasurementType.ENERGY_KWH)
                                                        .throughputUnit("tonne")
                                                        .build())
                                                .baselineData(BaselineData.builder()
                                                        .baselineDate(LocalDate.of(2024, 9, 2))
                                                        .energy(BigDecimal.valueOf(524660.807))
                                                        .throughput(BigDecimal.valueOf(28423.684))
                                                        .build())
                                                .targets(Targets.builder()
                                                        .improvement(BigDecimal.valueOf(2.582))
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .byPerformance(BigDecimal.valueOf(18.45867866278))
                        .numericalTarget(BigDecimal.valueOf(17.982978161707))
                        .tolerance(BigDecimal.ONE)
                        .build())
                .build();
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
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_prepopulated_not_equal_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .name("operatorName")
                        .build())
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder().build()))
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .measurementType(MeasurementType.CARBON_KG)
                                                        .throughputUnit("kg")
                                                        .build())
                                                .baselineData(BaselineData.builder()
                                                        .baselineDate(LocalDate.of(2025, 9, 2))
                                                        .energy(BigDecimal.valueOf(524661.807))
                                                        .throughput(BigDecimal.valueOf(28433.684))
                                                        .build())
                                                .targets(Targets.builder()
                                                        .improvement(BigDecimal.valueOf(2.583))
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();
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
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(6);
    }

    @Test
    void validate_with_NOVEM_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .name("operatorName")
                        .build())
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder().build()))
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .measurementType(MeasurementType.ENERGY_KWH)
                                                        .build())
                                                .baselineData(BaselineData.builder()
                                                        .baselineDate(LocalDate.of(2024, 9, 2))
                                                        .energy(BigDecimal.valueOf(524660.807))
                                                        .throughput(BigDecimal.valueOf(28423.684))
                                                        .build())
                                                .targets(Targets.builder()
                                                        .improvement(BigDecimal.valueOf(2.582))
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .targetType(AgreementCompositionType.NOVEM)
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
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat((result.getViolations().getFirst().getData()[0]).toString())
                .hasToString("E21 Prepopulated data does not match the active underlying agreement. Please download the latest version of the reporting spreadsheet for this target unit.");
    }

    @Test
    void validate_with_sector_throughput_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(TargetUnitAccountDetailsDTO.builder()
                        .businessId("businessId")
                        .name("operatorName")
                        .build())
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .sectorThroughputUnit("tonne")
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder().build()))
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .measurementType(MeasurementType.ENERGY_KWH)
                                                        .build())
                                                .baselineData(BaselineData.builder()
                                                        .baselineDate(LocalDate.of(2024, 9, 2))
                                                        .energy(BigDecimal.valueOf(524660.807))
                                                        .throughput(BigDecimal.valueOf(28423.684))
                                                        .build())
                                                .targets(Targets.builder()
                                                        .improvement(BigDecimal.valueOf(2.582))
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .performanceDataCalculatedMetrics(PerformanceDataCalculatedMetrics.builder()
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();
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
                        .byPerformance(BigDecimal.valueOf(18.45857866278))
                        .numericalTarget(BigDecimal.valueOf(17.981978161707))
                        .tolerance(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }
}
