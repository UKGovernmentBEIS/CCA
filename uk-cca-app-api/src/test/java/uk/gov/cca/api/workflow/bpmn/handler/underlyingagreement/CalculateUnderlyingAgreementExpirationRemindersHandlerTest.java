package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreement;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.cca.api.workflow.request.flow.common.service.CalculateAcceptanceExpirationRemindersService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;
import uk.gov.netz.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateUnderlyingAgreementExpirationRemindersHandlerTest {

    @InjectMocks
    private CalculateUnderlyingAgreementExpirationRemindersHandler handler;
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