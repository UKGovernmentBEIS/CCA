package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataContextValidatorTest {

    @InjectMocks
    private TP6PerformanceDataContextValidator validator;

    @Test
    void validate() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .targetPeriodDetails(TargetPeriodYearDTO.builder()
                        .performanceDataTemplateVersion("6.0")
                        .buyOutStartDate(LocalDate.of(2025, 1, 1))
                        .secondaryReportingStartDate(LocalDate.of(2025, 5, 1))
                        .build())
                .sectorAcronym("sector")
                .reportVersion(1)
                .fileName("ADS_1-T00020_TPR_TP6_V1.xlsx")
                .uploadedDate(LocalDate.of(2025, 2, 2))
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sector")
                .reportVersion(1)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .templateVersion("6")
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_filename_report_version_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .targetPeriodDetails(TargetPeriodYearDTO.builder()
                        .performanceDataTemplateVersion("6.0")
                        .buyOutStartDate(LocalDate.of(2025, 1, 1))
                        .secondaryReportingStartDate(LocalDate.of(2025, 5, 1))
                        .build())
                .sectorAcronym("sector")
                .reportVersion(1)
                .fileName("ADS_1-T00020_TPR_TP6_V2.xlsx")
                .uploadedDate(LocalDate.of(2025, 2, 2))
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sector")
                .reportVersion(1)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .templateVersion("6")
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat((result.getViolations().getFirst().getData()[0]).toString())
                .hasToString("The file name uploaded for this target unit uses a superseded version of the reporting spreadsheet. Please download the latest version of the reporting spreadsheet for this target unit.");
    }

    @Test
    void validate_report_version_for_primary_NOT_VALID() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .underlyingAgreement(UnderlyingAgreementDTO.builder()
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                                .targetComposition(TargetComposition.builder()
                                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .targetPeriodDetails(TargetPeriodYearDTO.builder()
                        .performanceDataTemplateVersion("6.0")
                        .buyOutStartDate(LocalDate.of(2025, 1, 1))
                        .secondaryReportingStartDate(LocalDate.of(2025, 5, 1))
                        .build())
                .sectorAcronym("sector")
                .reportVersion(2)
                .fileName("ADS_1-T00020_TPR_TP6_V2.xlsx")
                .uploadedDate(LocalDate.of(2025, 2, 2))
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .targetPeriod(PerformanceDataTargetPeriodType.TP6)
                .sector("sector")
                .reportVersion(2)
                .targetType(AgreementCompositionType.ABSOLUTE)
                .templateVersion("6")
                .build();

        // Invoke
        BusinessValidationResult result = validator.validate(referenceDetails, performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat((result.getViolations().getFirst().getData()[0]).toString())
                .hasToString("Correction to performance data is not allowed before the buy-out payment deadline");
    }
}
