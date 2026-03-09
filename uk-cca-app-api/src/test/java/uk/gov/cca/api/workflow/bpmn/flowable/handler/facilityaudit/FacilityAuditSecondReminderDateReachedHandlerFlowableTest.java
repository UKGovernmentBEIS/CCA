package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilityaudit;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.notification.FacilityAuditSendReminderNotificationService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityAuditSecondReminderDateReachedHandlerFlowableTest {

    @InjectMocks
    private FacilityAuditSecondReminderDateReachedHandlerFlowable handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private FacilityAuditSendReminderNotificationService reminderNotificationService;

    @Test
    void execute() {
        final String requestId = "1";
        final Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_AUDIT_EXPIRATION_DATE)).thenReturn(expirationDate);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_AUDIT_EXPIRATION_DATE);
        verify(reminderNotificationService, times(1)).sendSecondReminderNotification(requestId, expirationDate);
    }
}
