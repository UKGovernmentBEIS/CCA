package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service.FacilityCertificationAccountProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountProcessingHandler implements JavaDelegate {

    private final FacilityCertificationAccountProcessingService facilityCertificationAccountProcessingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final FacilityCertificationAccountState accountState = (FacilityCertificationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);

        try {
            facilityCertificationAccountProcessingService.doProcess(requestId, accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            accountState.getErrors().add(e.getMessage());
        }
    }
}
