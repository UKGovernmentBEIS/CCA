package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunInitiateCreateHandler implements JavaDelegate {

    private final FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception {
        if(execution.getProcessInstance().hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)) {
            List<String> providedAccountIds = (List<String>) execution.getProcessInstance().getVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
            CertificationPeriodType type = CertificationPeriodType.valueOf(
                    execution.getProcessInstance().getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_PERIOD).toString());

            facilityCertificationRunInitiateService.createFacilityCertificationRun(providedAccountIds, type);
        }
        else {
            facilityCertificationRunInitiateService.createFacilityCertificationRun();
        }
    }
}
