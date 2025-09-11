package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadSubmitInitializerTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadSubmitInitializer cut;
	
	@Test
	void getRequestTaskTypes() {
		assertThat(cut.getRequestTaskTypes())
				.containsExactly(CcaRequestTaskType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT);
	}
	
	@Test
	void initializePayload() {
		Request request = Request.builder().build();
		RequestTaskPayload result = cut.initializePayload(request);

		assertThat(result)
				.isInstanceOf(PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.class)
				.isEqualTo(PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
						.payloadType(CcaRequestTaskPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD)
						.processingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
						.build());
	}
}
