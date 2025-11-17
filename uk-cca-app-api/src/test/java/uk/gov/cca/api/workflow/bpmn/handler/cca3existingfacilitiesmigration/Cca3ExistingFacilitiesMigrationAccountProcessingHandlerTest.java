package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service.Cca3ExistingFacilitiesMigrationAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingHandlerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingHandler handler;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingService cca3ExistingFacilitiesMigrationAccountProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.getErrors()).isEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingService, times(1))
                .doProcess(requestId, accountState);
    }

    @Test
    void execute_throws_exception() throws Exception {
        final String requestId = "requestId";
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE)).thenReturn(accountState);
        doThrow(new BusinessException(ErrorCode.INTERNAL_SERVER))
                .when(cca3ExistingFacilitiesMigrationAccountProcessingService)
                .doProcess(requestId, accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.getErrors()).isNotEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingService, times(1))
                .doProcess(requestId, accountState);
    }
}
