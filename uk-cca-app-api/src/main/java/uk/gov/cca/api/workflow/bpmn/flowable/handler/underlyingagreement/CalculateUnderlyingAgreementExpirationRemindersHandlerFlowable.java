package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreement;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.cca.api.workflow.request.flow.common.service.CalculateAcceptanceExpirationRemindersService;
import uk.gov.netz.api.common.utils.DateUtils;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CalculateUnderlyingAgreementExpirationRemindersHandlerFlowable implements JavaDelegate {

	private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final CalculateAcceptanceExpirationRemindersService expirationRemindersService;
    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        LocalDate date = expirationRemindersService.getExpirationDate();
        Date expirationDate = DateUtils.convertLocalDateToDate(date);

        execution.setVariables(
                requestExpirationVarsBuilder
                        .buildExpirationVars(CcaRequestExpirationKey.UNDERLYING_AGREEMENT, expirationDate)
        );

        requestTaskTimeManagementService.setDueDateToTasks(requestId, CcaRequestExpirationKey.UNDERLYING_AGREEMENT, date);
    }
}
