package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataProcessingTriggerHandlerFlowable implements JavaDelegate {

    private final FacilityPerformanceAccountTemplateProcessingCreateRequestService facilityPerformanceAccountTemplateProcessingCreateRequestService;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) {
        final Long facilityId = (Long) execution.getVariable(CcaBpmnProcessConstants.FACILITY_ID);
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = (Map<Long, FacilityPerformanceAccountTemplateUploadReport>) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        facilityPerformanceAccountTemplateProcessingCreateRequestService.createRequest(facilityReports.get(facilityId), requestId);
    }
}
