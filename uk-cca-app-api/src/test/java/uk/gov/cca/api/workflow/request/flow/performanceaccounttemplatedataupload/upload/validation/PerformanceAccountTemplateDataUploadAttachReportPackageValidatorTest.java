package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadAttachReportPackageValidatorTest {
    @InjectMocks
    private PerformanceAccountTemplateDataUploadAttachReportPackageValidator validator;
    
    @Test
    void validate() {
        final FileDTO file = FileDTO.builder()
                .fileContent("test".getBytes())
                .fileName("test.zip")
                .build();
        
        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validate(file));
        
        // Verify
        assertThat(businessException.getErrorCode())
                .isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_FILE_TYPE);
    }
    
    @Test
    void getRequestTaskActionTypes() {
        assertThat(validator.getRequestTaskActionTypes())
                .containsExactly(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE);
    }
}
