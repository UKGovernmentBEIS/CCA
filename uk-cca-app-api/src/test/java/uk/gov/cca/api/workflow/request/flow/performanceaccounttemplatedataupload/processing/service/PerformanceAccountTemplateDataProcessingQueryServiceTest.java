package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataProcessingQueryServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateDataProcessingQueryService cut;
	
	@Mock
    private RequestService requestService;
	
	@Test
	void getAccountReports() {
		String requestId = "req1";
		PerformanceAccountTemplateDataProcessingRequestPayload requestPayload = PerformanceAccountTemplateDataProcessingRequestPayload.builder()
				.accountFileReports(Map.of(
						1L, AccountUploadReport.builder().accountId(1L).build()
						))
				.build();
		
		Request request = Request.builder()
				.payload(requestPayload)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		
		var result = cut.getAccountReports(requestId);
		
		assertThat(result).isEqualTo(requestPayload.getAccountFileReports());
		
		verify(requestService, times(1)).findRequestById(requestId);
    }

}
