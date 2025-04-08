package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUpload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadExcelFileNameValidatorTest {

    @InjectMocks
    private PerformanceDataUploadExcelFileNameValidator validator;

    @Test
    void validate() {
        final String filename = "ADS_1-T00001_TPR_TP6_V1.xlsx";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .acronym("ADS_1")
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .build();
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        // Invoke
        BusinessValidationResult result = validator.validate(filename, sectorAssociationInfo, performanceDataUpload, accountsMap);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_not_matching_regex() {
        final String filename = "ADS_1-T00001_TPR_TP60_V1.xlsx";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .acronym("ADS_1")
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .build();
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        // Invoke
        BusinessValidationResult result = validator.validate(filename, sectorAssociationInfo, performanceDataUpload, accountsMap);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat(result.getViolations().getFirst())
                .isEqualTo(new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.FILE_NAME_NOT_VALID));
    }

    @Test
    void validate_not_matching_sector() {
        final String filename = "ADS_2-T00001_TPR_TP6_V1.xlsx";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .acronym("ADS_1")
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .build();
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        // Invoke
        BusinessValidationResult result = validator.validate(filename, sectorAssociationInfo, performanceDataUpload, accountsMap);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat(result.getViolations().getFirst())
                .isEqualTo(new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.SECTOR_NOT_VALID));
    }

    @Test
    void validate_no_account_found() {
        final String filename = "ADS_1-T00002_TPR_TP6_V1.xlsx";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .acronym("ADS_1")
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .build();
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        // Invoke
        BusinessValidationResult result = validator.validate(filename, sectorAssociationInfo, performanceDataUpload, accountsMap);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(1);
        assertThat(result.getViolations().getFirst())
                .isEqualTo(new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.TU_NOT_VALID));
    }
}
