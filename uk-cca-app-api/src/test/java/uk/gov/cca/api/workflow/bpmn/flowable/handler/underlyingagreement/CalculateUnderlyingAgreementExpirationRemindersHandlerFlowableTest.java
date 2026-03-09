package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreement;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.cca.api.workflow.request.flow.common.service.CalculateAcceptanceExpirationRemindersService;
import uk.gov.netz.api.common.utils.DateUtils;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateUnderlyingAgreementExpirationRemindersHandlerFlowableTest {

    @InjectMocks
    private CalculateUnderlyingAgreementExpirationRemindersHandlerFlowable handler;
    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    @Mock
    private DelegateExecution execution;
    @Mock
    private CalculateAcceptanceExpirationRemindersService expirationRemindersService;
    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Test
    void execute() {
        final LocalDate date = LocalDate.of(2020, 1, 3);
        Date expirationDate = DateUtils.convertLocalDateToDate(date);
        String requestId = "requestId";

        when(expirationRemindersService.getExpirationDate()).thenReturn(date);
        when(requestExpirationVarsBuilder.buildExpirationVars(CcaRequestExpirationKey.UNDERLYING_AGREEMENT, expirationDate))
                .thenReturn(Map.of());
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(expirationRemindersService.getExpirationDate()).thenReturn(date);
        // Invoke
        handler.execute(execution);

        // Verify
        verify(expirationRemindersService, times(1))
                .getExpirationDate();
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(CcaRequestExpirationKey.UNDERLYING_AGREEMENT, expirationDate);
        verify(expirationRemindersService, times(1))
                .getExpirationDate();
        verify(requestTaskTimeManagementService, times(1))
                .setDueDateToTasks(requestId, CcaRequestExpirationKey.UNDERLYING_AGREEMENT, date);
    }
}