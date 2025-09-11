package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class FacilityCertificationAccountProcessingMarkAsCompletedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final FacilityCertificationAccountState accountState = (FacilityCertificationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);

        if(accountState.getErrors().isEmpty()) {
            accountState.setSucceeded(true);
        }
        else {
            accountState.setSucceeded(false);
            accountState.setFacilitiesCertified(0L);
        }

        // Close request
        execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
