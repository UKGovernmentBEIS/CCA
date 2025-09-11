package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingRequestIdGeneratorTest {

	@InjectMocks
    private PerformanceAccountTemplateProcessingRequestIdGenerator cut;
	
	@Test
    void getPrefix() {
        assertThat(cut.getPrefix()).isEqualTo("PAT");
    }

    @Test
    void getTypes() {
        assertThat(cut.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING);
    }
    
    @Test
    void generate() {
    	PerformanceAccountTemplateProcessingRequestMetadata metadata = PerformanceAccountTemplateProcessingRequestMetadata.builder()
    			.accountBusinessId("accBusId")
    			.targetPeriodType(TargetPeriodType.TP6)
    			.targetPeriodYear(Year.of(2025))
    			.reportVersion(2)
    			.build();
    	
    	RequestParams params = RequestParams.builder()
    			.requestMetadata(metadata)
    			.build();
    	
    	String result = cut.generate(params);
    	
    	assertThat(result).isEqualTo("accBusId-PAT-TP6-2025-V2");
    }
    
}
