package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceDataUploadCompletedService {

	private final RequestTaskService requestTaskService;
	private final PerformanceDataUploadService performanceDataUploadService;

	@Transactional
	public void completed(String requestId, Map<Long, TargetUnitAccountUploadReport> accountReports, String errorMessage) {
		final RequestTask requestTask = requestTaskService
				.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT, requestId);
		final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
				(PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

		taskPayload.setErrorMessage(errorMessage);
		taskPayload.setAccountReports(accountReports);
		taskPayload.setProcessCompleted(true);

		// Update files succeeded / failed
		long succeeded = accountReports.values().stream().filter(TargetUnitAccountUploadReport::isSucceeded).count();
		int failed = taskPayload.getTotalFilesUploaded() - (int) succeeded;
		updateNumOfFailedSucceededFiles(taskPayload, Optional.of((int) succeeded), Optional.of(failed));
	}

	@Transactional
	public void completedDueToEmptyAccountReports(RequestTask requestTask) {
		PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
				(PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

		try {
			performanceDataUploadService.createCsvFile(requestTask, Map.of());
		} catch (Exception ex) {
			taskPayload.setErrorMessage(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.GENERATE_CSV_FAILED.name());
		}

		taskPayload.setProcessCompleted(true);
		updateNumOfFailedSucceededFiles(taskPayload, Optional.empty(), Optional.of(taskPayload.getTotalFilesUploaded()));
	}

	private void updateNumOfFailedSucceededFiles(PerformanceDataUploadSubmitRequestTaskPayload taskPayload, Optional<Integer> filesSucceeded,
												Optional<Integer> filesFailed) {
		filesSucceeded.ifPresent(taskPayload::setFilesSucceeded);
		filesFailed.ifPresent(taskPayload::setFilesFailed);
	}
}
