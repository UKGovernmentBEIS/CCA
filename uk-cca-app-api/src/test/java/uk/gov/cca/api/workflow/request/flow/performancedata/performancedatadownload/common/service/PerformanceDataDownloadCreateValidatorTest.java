package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadCreateValidatorTest {

    @InjectMocks
    private PerformanceDataDownloadCreateValidator performanceDataDownloadCreateValidator;

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(performanceDataDownloadCreateValidator.getMutuallyExclusiveRequests())
                .containsExactly(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }

    @Test
    void getRequestType() {
        assertThat(performanceDataDownloadCreateValidator.getRequestType())
                .isEqualTo(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }
}
