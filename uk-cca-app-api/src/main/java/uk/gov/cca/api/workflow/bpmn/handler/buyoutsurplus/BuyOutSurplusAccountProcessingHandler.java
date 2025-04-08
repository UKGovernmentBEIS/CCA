package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.validation.BuyOutSurplusViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service.BuyOutSurplusAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingHandler implements JavaDelegate {

    private final BuyOutSurplusAccountProcessingService buyOutSurplusAccountProcessingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String errorMessage = BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PROCESS_FAILED.getMessage();
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final BuyOutSurplusAccountState accountState = (BuyOutSurplusAccountState) execution
                .getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);

        try {
            buyOutSurplusAccountProcessingService.doProcess(requestId, accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            accountState.getErrors().add(errorMessage);
        }
    }
}
