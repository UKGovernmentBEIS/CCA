package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadRequestIdGeneratorTest {

    @InjectMocks
    private PerformanceDataUploadRequestIdGenerator generator;

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("TPRUL");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_DATA_UPLOAD);
    }
}
