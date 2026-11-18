package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.run.service.FacilityCertificationRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountProcessingCompletedHandler implements JavaDelegate {

    private final FacilityCertificationRunService facilityCertificationRunService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final FacilityCertificationAccountState accountState = (FacilityCertificationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);

        facilityCertificationRunService.accountProcessingCompleted(requestId, accountId, accountState);

        // Increment completed number var
        final Integer numberOfAccountsCompleted = (Integer) execution
                .getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
        execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
    }
}
