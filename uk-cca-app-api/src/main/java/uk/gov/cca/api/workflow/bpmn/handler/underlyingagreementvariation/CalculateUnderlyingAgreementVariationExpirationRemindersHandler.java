package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.cca.api.workflow.request.flow.common.service.CalculateAcceptanceExpirationRemindersService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;
import uk.gov.netz.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CalculateUnderlyingAgreementVariationExpirationRemindersHandler implements JavaDelegate {

	private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final RequestTaskTimeManagementService requestTaskTimeManagementService;
    private final CalculateAcceptanceExpirationRemindersService expirationRemindersService;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        LocalDate date = expirationRemindersService.getExpirationDate();
        Date expirationDate = DateUtils.convertLocalDateToDate(date);

        execution.setVariables(
                requestExpirationVarsBuilder
                        .buildExpirationVars(CcaRequestExpirationKey.UNDERLYING_AGREEMENT_VARIATION, expirationDate)
        );

        requestTaskTimeManagementService
                .setDueDateToTasks(requestId, CcaRequestExpirationKey.UNDERLYING_AGREEMENT_VARIATION, date);
    }
}
