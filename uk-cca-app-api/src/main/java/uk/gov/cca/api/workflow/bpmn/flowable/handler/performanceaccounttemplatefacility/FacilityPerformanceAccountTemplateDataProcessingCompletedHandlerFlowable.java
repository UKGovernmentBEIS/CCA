package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataProcessingCompletedHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Request request = requestService.findRequestById(requestId);
        final FacilityPerformanceAccountTemplateDataProcessingRequestPayload requestPayload =
                (FacilityPerformanceAccountTemplateDataProcessingRequestPayload) request.getPayload();
        try {
            Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = requestPayload.getFacilityReports();

            ObjectMapper mapper = new ObjectMapper();
            execution.setVariable(CcaBpmnProcessConstants.FACILITY_REPORTS, mapper.writeValueAsString(facilityReports));
            execution.setVariable(CcaBpmnProcessConstants.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING_MESSAGE_FAILED, false);
        } catch (JsonProcessingException e) {
            log.error("Cannot generate message for request {}", requestPayload.getParentRequestId(), e);
            execution.setVariable(CcaBpmnProcessConstants.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING_MESSAGE_FAILED, true);
        } finally {
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
