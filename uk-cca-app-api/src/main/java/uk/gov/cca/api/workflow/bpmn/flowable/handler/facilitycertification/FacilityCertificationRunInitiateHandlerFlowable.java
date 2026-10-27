package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunInitiateHandlerFlowable implements JavaDelegate {

    private final FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Override
    public void execute(DelegateExecution execution) {
        boolean canBeInitiated = facilityCertificationRunInitiateService.isValidForFacilityCertificationRun();
        execution.setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, canBeInitiated);
    }
}
