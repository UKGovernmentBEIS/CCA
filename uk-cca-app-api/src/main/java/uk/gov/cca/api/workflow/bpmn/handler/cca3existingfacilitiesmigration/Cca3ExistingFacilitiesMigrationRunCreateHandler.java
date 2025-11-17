package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.service.Cca3ExistingFacilitiesMigrationCreateRunService;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationRunCreateHandler implements JavaDelegate {

    private final Cca3ExistingFacilitiesMigrationCreateRunService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        service.createCca3ExistingFacilitiesMigrationRun();
    }
}
