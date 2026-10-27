package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.utils.StringSafetyUtils;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service.FacilityCertificationAccountProcessingService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountProcessingHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
    private final FacilityCertificationAccountProcessingService facilityCertificationAccountProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final FacilityCertificationAccountState accountState = (FacilityCertificationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);

        try {
            final Request request = requestService.findRequestById(requestId);
            facilityCertificationAccountProcessingService.doProcess(request, accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            accountState.getErrors().add(StringSafetyUtils.safeErrorMessage(e));
        }
    }
}
