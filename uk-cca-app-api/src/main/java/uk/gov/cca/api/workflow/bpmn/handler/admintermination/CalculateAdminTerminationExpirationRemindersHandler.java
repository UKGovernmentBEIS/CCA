package uk.gov.cca.api.workflow.bpmn.handler.admintermination;

import java.time.LocalDate;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.CalculateAdminTerminationWithdrawExpirationRemindersService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.netz.api.common.utils.DateUtils;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

@Service
@RequiredArgsConstructor
public class CalculateAdminTerminationExpirationRemindersHandler implements JavaDelegate {

	private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
	private final CalculateAdminTerminationWithdrawExpirationRemindersService calculateAdminTerminationWithdrawExpirationRemindersService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		LocalDate date = calculateAdminTerminationWithdrawExpirationRemindersService.getExpirationDate();
		Date expirationDate = DateUtils.convertLocalDateToDate(date);

        execution.setVariables(requestExpirationVarsBuilder.buildExpirationVars(CcaRequestExpirationKey.ADMIN_TERMINATION, expirationDate));
	}
}
