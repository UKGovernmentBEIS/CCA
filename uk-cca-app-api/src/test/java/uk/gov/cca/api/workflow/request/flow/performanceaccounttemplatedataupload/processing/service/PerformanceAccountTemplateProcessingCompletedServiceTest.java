package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class PerformanceAccountTemplateProcessingCompletedServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateProcessingCompletedService cut;
	
	@Mock
	private RequestService requestService;
	
	@Test
	void completed() {
		String requestId = "reqId";
		Long accountId = 1L;
		AccountUploadReport accountReport = AccountUploadReport.builder().accountId(accountId).build();
		
		PerformanceAccountTemplateDataProcessingRequestPayload requestPayload = PerformanceAccountTemplateDataProcessingRequestPayload.builder()
				.businessId("businessId")
				.build();
		
		Request request = Request.builder().payload(requestPayload).build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		
		cut.completed(requestId, accountId, accountReport);
		
		assertThat(requestPayload.getAccountFileReports()).hasSize(1);
		assertThat(requestPayload.getAccountFileReports().get(accountId)).isEqualTo(accountReport);
		
		verify(requestService, times(1)).findRequestById(requestId);
	}
}
