package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadSubmitService {

	@Transactional
	public void submitUpload(RequestTask requestTask,
			PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload requestTaskActionPayload,
			FileReports fileReports) {
		final PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = (PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask
				.getPayload();
		
		requestTaskPayload.setPerformanceAccountTemplateDataUpload(
				requestTaskActionPayload.getPerformanceAccountTemplateDataUpload());
		requestTaskPayload.setFileReports(fileReports);
		requestTaskPayload.setProcessingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS);
	}
}
