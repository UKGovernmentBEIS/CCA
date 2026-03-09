package uk.gov.cca.api.workflow.bpmn.camunda.handler.underlyingagreement;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.SendReminderNotificationService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementFirstReminderDateReachedHandler implements JavaDelegate {

	private final RequestService requestService;
	private final SendReminderNotificationService reminderNotificationService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Date expirationDate = (Date) execution.getVariable(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_EXPIRATION_DATE);

		final Request request = requestService.findRequestById(requestId);
		final String regulatorAssignee = request.getPayload().getRegulatorAssignee();

		reminderNotificationService.sendFirstReminderNotification(request, expirationDate, regulatorAssignee);
	}
}
