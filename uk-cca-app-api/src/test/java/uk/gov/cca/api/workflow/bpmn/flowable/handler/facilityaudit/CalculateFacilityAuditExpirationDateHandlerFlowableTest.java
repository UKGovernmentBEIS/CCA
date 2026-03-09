package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilityaudit;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService;
import uk.gov.netz.api.common.utils.DateUtils;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateFacilityAuditExpirationDateHandlerFlowableTest {

    @InjectMocks
    private CalculateFacilityAuditExpirationDateHandlerFlowable handler;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";
        final LocalDate expirationDate = LocalDate.of(2022, 10, 12);
        final Date deadline = DateUtils.convertLocalDateToDate(expirationDate);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService.calculateExpirationDate(requestId))
                .thenReturn(Optional.of(expirationDate));
        when(requestExpirationVarsBuilder.buildExpirationVars(CcaRequestExpirationKey.FACILITY_AUDIT, deadline))
                .thenReturn(anyMap());

        // invoke
        handler.execute(execution);

        // verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService, times(1)).calculateExpirationDate(requestId);
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(CcaRequestExpirationKey.FACILITY_AUDIT, deadline);
        verify(requestTaskTimeManagementService, times(1)).setDueDateToTasks(requestId, CcaRequestExpirationKey.FACILITY_AUDIT, expirationDate);
        verify(execution, never()).setVariable(CcaBpmnProcessConstants.FACILITY_AUDIT_EXPIRATION_DATE, expirationDate);
    }

    @Test
    void execute_with_null_expiration_date() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService.calculateExpirationDate(requestId))
                .thenReturn(Optional.empty());

        // invoke
        handler.execute(execution);

        // verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService, times(1)).calculateExpirationDate(requestId);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.FACILITY_AUDIT_EXPIRATION_DATE, null);
        verify(requestTaskTimeManagementService, times(1)).setDueDateToTasks(requestId, CcaRequestExpirationKey.FACILITY_AUDIT, null);
        verify(requestExpirationVarsBuilder, never()).buildExpirationVars(any(), any());
    }
}
