package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform.PerformanceDataFacilityProcessingSubmittedMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingResults;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityProcessingService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityProcessingHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
    private final PerformanceDataFacilityProcessingService performanceDataFacilityProcessingService;
    private static final PerformanceDataFacilityProcessingSubmittedMapper CSV_FORM_SUBMITTED_MAPPER = Mappers
            .getMapper(PerformanceDataFacilityProcessingSubmittedMapper.class);

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final FacilityUploadReport facilityUploadReport = (FacilityUploadReport) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        try {
            final Request request = requestService.findRequestById(requestId);
            final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                    (PerformanceDataFacilityProcessingRequestPayload) request.getPayload();
            PerformanceDataFacilityProcessingRequestMetadata metadata =
                    (PerformanceDataFacilityProcessingRequestMetadata) request.getMetadata();

            PerformanceDataFacilityProcessingResults results = performanceDataFacilityProcessingService
                    .doProcess(requestPayload, facilityUploadReport);

            if(facilityUploadReport.getErrors().isEmpty()) {
                // Set metadata
                metadata.setReportVersion(results.getReportVersion());

                // Add timeline
                PerformanceDataFacilitySubmittedRequestActionPayload actionPayload = CSV_FORM_SUBMITTED_MAPPER
                        .toPerformanceDataFacilitySubmittedRequestActionPayload(request, results);
                requestService.addActionToRequest(request,
                        actionPayload,
                        CcaRequestActionType.PERFORMANCE_DATA_FACILITY_PROCESSING_SUBMITTED,
                        requestPayload.getSectorUserAssignee());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            facilityUploadReport.getErrors()
                    .add(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_PROCESS_FAILED.getMessage());
        }
    }
}
