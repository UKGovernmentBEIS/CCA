package uk.gov.cca.api.workflow.bpmn.handler.admintermination;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.CalculateAdminTerminationWithdrawExpirationRemindersService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateAdminTerminationExpirationRemindersHandlerTest {

    @InjectMocks
    private CalculateAdminTerminationExpirationRemindersHandler handler;

    @Mock
    private CalculateAdminTerminationWithdrawExpirationRemindersService calculateAdminTerminationWithdrawExpirationRemindersService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final LocalDate date = LocalDate.of(2020, 1, 3);
        Date expirationDate = DateUtils.convertLocalDateToDate(date);

        when(calculateAdminTerminationWithdrawExpirationRemindersService.getExpirationDate())
                .thenReturn(date);
        when(requestExpirationVarsBuilder.buildExpirationVars(CcaRequestExpirationKey.ADMIN_TERMINATION, expirationDate))
                .thenReturn(Map.of());

        // Invoke
        handler.execute(execution);

        // Verify
        verify(calculateAdminTerminationWithdrawExpirationRemindersService, times(1))
                .getExpirationDate();
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(CcaRequestExpirationKey.ADMIN_TERMINATION, expirationDate);
    }
}
