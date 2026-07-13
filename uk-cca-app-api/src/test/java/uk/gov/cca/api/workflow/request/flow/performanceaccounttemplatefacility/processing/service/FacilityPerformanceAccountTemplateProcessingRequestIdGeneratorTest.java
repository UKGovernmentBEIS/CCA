package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateProcessingRequestIdGeneratorTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateProcessingRequestIdGenerator generator;

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("PAT");
    }
}
