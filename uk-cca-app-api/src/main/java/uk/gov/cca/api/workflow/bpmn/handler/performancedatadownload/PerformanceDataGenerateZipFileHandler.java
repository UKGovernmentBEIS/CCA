package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataGenerateZipFileService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataGenerateZipFileHandler implements JavaDelegate {

	private final PerformanceDataGenerateZipFileService performanceDataGenerateZipFileService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final FileInfoDTO zipFile = performanceDataGenerateZipFileService.generateZipFile(requestId);
		execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ZIP_FILE, zipFile);

		// Set workflow outputs to null
		execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE, null);
	}
}