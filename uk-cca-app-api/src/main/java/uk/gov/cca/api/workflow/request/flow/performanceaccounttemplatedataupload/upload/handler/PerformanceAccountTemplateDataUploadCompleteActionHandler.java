package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@Component
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadCompleteActionHandler
		implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

	private final WorkflowService workflowService;
	private final RequestTaskService requestTaskService;

	@Override
	public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
			RequestTaskActionEmptyPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		final PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = (PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask
				.getPayload();

		// Check that process has been completed
		if (requestTaskPayload
				.getProcessingStatus() != PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED) {
			throw new BusinessException(CcaErrorCode.PERFORMANCE_ACCOUNT_TEMPLATE_NOT_COMPLETED_YET);
		}

		// Set request's submission date
		requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

		// Complete
		workflowService.completeTask(requestTask.getProcessTaskId());

		return requestTask.getPayload();
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE);
	}

}
