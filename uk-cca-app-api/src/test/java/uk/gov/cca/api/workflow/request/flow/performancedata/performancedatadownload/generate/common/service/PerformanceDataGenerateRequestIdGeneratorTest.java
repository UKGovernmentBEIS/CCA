package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateRequestIdGeneratorTest {

    @InjectMocks
    private PerformanceDataGenerateRequestIdGenerator generator;

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("TPRGN");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_DATA_GENERATE);
    }
}
