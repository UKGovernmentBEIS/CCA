package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunInitiateHandler implements JavaDelegate {

    private final FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        boolean canBeInitiated = execution.getProcessInstance().hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)
                || facilityCertificationRunInitiateService.isValidForFacilityCertificationRun();
        execution.setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, canBeInitiated);
    }
}
