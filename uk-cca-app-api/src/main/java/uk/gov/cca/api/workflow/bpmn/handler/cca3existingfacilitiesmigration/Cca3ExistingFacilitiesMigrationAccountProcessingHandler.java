package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service.Cca3ExistingFacilitiesMigrationAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingHandler implements JavaDelegate {

    private final Cca3ExistingFacilitiesMigrationAccountProcessingService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        Cca3FacilityMigrationAccountState accountState = (Cca3FacilityMigrationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);

        try {
            service.doProcess(requestId, accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            accountState.getErrors().add(e.getMessage());
        }
    }
}
