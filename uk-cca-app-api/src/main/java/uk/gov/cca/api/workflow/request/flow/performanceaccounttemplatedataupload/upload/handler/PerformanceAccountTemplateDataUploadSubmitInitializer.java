package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

@Service
public class PerformanceAccountTemplateDataUploadSubmitInitializer implements InitializeRequestTaskHandler {

	@Override
	public RequestTaskPayload initializePayload(Request request) {
		return PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
				.payloadType(CcaRequestTaskPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD)
				.processingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
				.build();
	}

	@Override
	public Set<String> getRequestTaskTypes() {
		return Set.of(CcaRequestTaskType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT);
	}

}
