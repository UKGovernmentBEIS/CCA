package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadCreateValidatorTest {

    @InjectMocks
    private PerformanceDataUploadCreateValidator performanceDataUploadCreateValidator;

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(performanceDataUploadCreateValidator.getMutuallyExclusiveRequests())
                .containsExactly(CcaRequestType.PERFORMANCE_DATA_UPLOAD);
    }

    @Test
    void getRequestType() {
        assertThat(performanceDataUploadCreateValidator.getRequestType())
                .isEqualTo(CcaRequestType.PERFORMANCE_DATA_UPLOAD);
    }
}
