package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataProcessingSubmittedHandlerFlowable implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        //TODO:
        // maybe not necessary service
    }
}
