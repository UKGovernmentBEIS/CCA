package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.run.service.Cca3ExistingFacilitiesMigrationRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingCompletedHandler implements JavaDelegate {

    private final Cca3ExistingFacilitiesMigrationRunService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final Cca3FacilityMigrationAccountState accountState = (Cca3FacilityMigrationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);

        service.accountProcessingCompleted(requestId, accountId, accountState);

        // Increment completed number var
        final Integer numberOfAccountsCompleted = (Integer) execution
                .getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
        execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
    }
}
