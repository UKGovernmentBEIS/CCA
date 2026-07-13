package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service.PerformanceDataFacilityDataUploadCompleteService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataProcessingMessageFailedHandlerFlowable implements JavaDelegate {

    private final PerformanceDataFacilityDataUploadCompleteService performanceDataFacilityDataUploadCompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        performanceDataFacilityDataUploadCompleteService.processMessageFailed(requestId);
    }
}
