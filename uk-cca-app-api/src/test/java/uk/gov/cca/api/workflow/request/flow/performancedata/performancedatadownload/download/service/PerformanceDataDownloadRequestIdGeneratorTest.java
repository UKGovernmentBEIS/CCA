package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadRequestIdGeneratorTest {

    @InjectMocks
    private PerformanceDataDownloadRequestIdGenerator generator;

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("TPRDL");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }
}
