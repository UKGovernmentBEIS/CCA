package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataSpreadsheetTemplateValidatorTest {

    @InjectMocks
    private TP6PerformanceDataSpreadsheetTemplateValidator tp6PerformanceDataSpreadsheetTemplateValidator;

    @Mock
    private DataValidator<TP6PerformanceData> validator;

    @Spy
    private ArrayList<TP6PerformanceDataSectionContextValidator> performanceDataSectionContextValidators;

    @Mock
    private TP6PerformanceDataTargetUnitDetailsContextValidator tp6PerformanceDataTargetUnitDetailsContextValidator;

    @BeforeEach
    void setUp() {
        performanceDataSectionContextValidators.add(tp6PerformanceDataTargetUnitDetailsContextValidator);
    }

    @Test
    void validateData() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .build();

        when(validator.validate(eq(performanceData), any())).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = tp6PerformanceDataSpreadsheetTemplateValidator.validateData(performanceData);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(eq(performanceData), any());
    }

    @Test
    void validateData_with_errors() {
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .build();

        when(validator.validate(eq(performanceData), any()))
                .thenReturn(Optional.of(new BusinessViolation("section", List.of("error1"))));

        // Invoke
        BusinessValidationResult result = tp6PerformanceDataSpreadsheetTemplateValidator.validateData(performanceData);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(validator, times(1)).validate(eq(performanceData), any());
    }

    @Test
    void validateBusinessData() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().reportVersion(1).build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder().type(PerformanceDataTargetPeriodType.TP6).build();

        when(tp6PerformanceDataTargetUnitDetailsContextValidator.validate(referenceDetails, performanceData))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        List<BusinessValidationResult> results = tp6PerformanceDataSpreadsheetTemplateValidator
                .validateBusinessData(referenceDetails, performanceData);

        // Verify
        assertThat(results).extracting(BusinessValidationResult::isValid)
                .containsExactly(true);
        verify(tp6PerformanceDataTargetUnitDetailsContextValidator, times(1))
                .validate(referenceDetails, performanceData);
    }

    @Test
    void validateBusinessData_with_errors() {
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder().reportVersion(1).build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder().type(PerformanceDataTargetPeriodType.TP6).build();

        when(tp6PerformanceDataTargetUnitDetailsContextValidator.validate(referenceDetails, performanceData))
                .thenReturn(BusinessValidationResult.invalid(List.of(
                        new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA)))
                );

        // Invoke
        List<BusinessValidationResult> results = tp6PerformanceDataSpreadsheetTemplateValidator
                .validateBusinessData(referenceDetails, performanceData);

        // Verify
        assertThat(results).extracting(BusinessValidationResult::isValid)
                .containsExactly(false);
        verify(tp6PerformanceDataTargetUnitDetailsContextValidator, times(1))
                .validate(referenceDetails, performanceData);
    }

    @Test
    void getDocumentTemplateType() {
        assertThat(tp6PerformanceDataSpreadsheetTemplateValidator.getDocumentTemplateType())
                .isEqualTo(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
    }
}
