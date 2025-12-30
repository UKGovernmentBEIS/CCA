package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingMarkAsCompletedHandler implements JavaDelegate {

    private final FileAttachmentService fileAttachmentService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Cca3FacilityMigrationAccountState accountState = (Cca3FacilityMigrationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);

        if(accountState.getErrors().isEmpty()) {
            accountState.setSucceeded(true);
        }
        else {
            accountState.setSucceeded(false);

            // Close request
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);

            // Clear attachments
            Set<String> attachments = accountState.getFacilityMigrationDataList().stream()
                    .map(Cca3FacilityMigrationData::getCalculatorFileUuid)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if(!attachments.isEmpty()) {
                fileAttachmentService.deleteFileAttachments(attachments);
            }
        }
    }
}
