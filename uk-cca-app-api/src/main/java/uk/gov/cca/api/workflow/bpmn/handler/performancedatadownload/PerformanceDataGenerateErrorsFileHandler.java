package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataGenerateErrorsFileService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceDataGenerateErrorsFileHandler implements JavaDelegate {

	private final PerformanceDataGenerateErrorsFileService performanceDataGenerateErrorsFileService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Optional<FileInfoDTO> errorsFileOpt = performanceDataGenerateErrorsFileService.generateErrorsFile(requestId);
		execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE, errorsFileOpt.orElse(null));
	}
}