package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service.PerformanceDataFacilityDataUploadCompleteService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadCompletedHandlerFlowable implements JavaDelegate {

    private final PerformanceDataFacilityDataUploadCompleteService performanceDataFacilityDataUploadCompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        Map<String, ?> rawMap = (Map<String, ?>) execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);

        // Convert to Map<Long, FacilityUploadReport>
        ObjectMapper mapper = new ObjectMapper();
        Map<Long, FacilityUploadReport> facilityReports = rawMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getKey()),
                        e -> mapper.convertValue(e.getValue(), FacilityUploadReport.class)
                ));

        performanceDataFacilityDataUploadCompleteService.processCompleted(requestId, facilityReports);
    }
}
