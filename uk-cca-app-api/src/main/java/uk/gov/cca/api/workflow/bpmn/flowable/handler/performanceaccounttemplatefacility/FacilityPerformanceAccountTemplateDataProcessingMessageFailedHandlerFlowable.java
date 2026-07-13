package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service.FacilityPerformanceAccountTemplateDataUploadCompleteService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataProcessingMessageFailedHandlerFlowable implements JavaDelegate {

    private final FacilityPerformanceAccountTemplateDataUploadCompleteService performanceAccountTemplateDataUploadCompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        performanceAccountTemplateDataUploadCompleteService.processMessageFailed(requestId);
    }
}
