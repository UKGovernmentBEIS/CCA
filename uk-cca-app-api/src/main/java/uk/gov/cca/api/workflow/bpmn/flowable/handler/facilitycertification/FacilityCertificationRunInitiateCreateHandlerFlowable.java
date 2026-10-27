package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunInitiateCreateHandlerFlowable implements JavaDelegate {

    private final FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Override
    public void execute(DelegateExecution execution) {
        facilityCertificationRunInitiateService.createFacilityCertificationRun();
    }
}
