package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.utils.PerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadProcessingCompletedService {

	private final RequestTaskService requestTaskService;
	private final PerformanceAccountTemplateDataGenerateCsvReportService generateCsvReportService;

	@Transactional
	public void completed(String requestId, Map<Long, AccountUploadReport> accountReports) {
		final RequestTask requestTask = requestTaskService
				.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
		final PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = (PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask
				.getPayload();

		// update reports of request task payload
		requestTaskPayload.getFileReports().getAccountFileReports().putAll(accountReports);

		// generate csv report file
		try {
			generateCsvReportService.generateCsvReport(requestTask.getId(), requestTaskPayload.getFileReports());
		} catch (Exception e) {
			log.error("CSV report file generation failed", e);
			requestTaskPayload.setErrorType(PerformanceAccountTemplateDataUploadErrorType.CSV_GENERATION_FAILED);
		}
		
		// set processing status to completed
		requestTaskPayload.setProcessingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
	}
}
