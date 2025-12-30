package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingMarkAsCompletedHandlerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingMarkAsCompletedHandler handler;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.isSucceeded()).isTrue();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);
        verifyNoMoreInteractions(execution);
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void execute_with_errors() throws Exception {
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .accountId(1L)
                .errors(List.of("error1", "error2"))
                .facilityMigrationDataList(List.of(
                        Cca3FacilityMigrationData.builder().calculatorFileUuid("file1").build(),
                        Cca3FacilityMigrationData.builder().calculatorFileUuid("file2").build(),
                        Cca3FacilityMigrationData.builder().build()
                ))
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.isSucceeded()).isFalse();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        verify(fileAttachmentService, times(1)).deleteFileAttachments(Set.of("file1", "file2"));
    }
}
