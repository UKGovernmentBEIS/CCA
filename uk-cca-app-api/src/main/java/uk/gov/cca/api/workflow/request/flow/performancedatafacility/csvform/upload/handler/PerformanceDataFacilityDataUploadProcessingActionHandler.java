package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service.PerformanceDataFacilityDataUploadService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadProcessingActionHandler implements RequestTaskActionHandler<PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataFacilityDataUploadService performanceDataFacilityDataUploadService;
    private final WorkflowService workflowService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                                      PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload taskActionPayload) {
        final LocalDateTime submissionDate = LocalDateTime.now();
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        // Process data and validate
        performanceDataFacilityDataUploadService.process(requestTask, taskActionPayload, submissionDate);

        // Create processing workflow
        final SectorAssociationInfo sectorAssociation = taskPayload.getSectorAssociationInfo();
        final Map<Long, FacilityUploadReport> facilityReports = taskPayload.getFacilityReports();
        final String uploadRequestBusinessKey = (String) workflowService
                .getVariable(request.getProcessInstanceId(), BpmnProcessConstants.BUSINESS_KEY);

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_FACILITY_DATA_PROCESSING)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, sectorAssociation.getId().toString(),
                        ResourceType.CA, sectorAssociation.getCompetentAuthority().name()
                ))
                .requestPayload(PerformanceDataFacilityDataProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_FACILITY_DATA_PROCESSING_PAYLOAD)
                        .parentRequestId(request.getId())
                        .sectorAssociationInfo(sectorAssociation)
                        .sectorUserAssignee(appUser.getUserId())
                        .targetPeriodType(taskActionPayload.getPerformanceDataUpload().getTargetPeriodType())
                        .reportType(taskActionPayload.getPerformanceDataUpload().getReportType())
                        .submissionDate(submissionDate)
                        .build())
                .processVars(Map.of(
                        // Wrap to hashset to be serializable (HashMap.Keyset is not serializable)
                        CcaBpmnProcessConstants.FACILITY_IDS, new HashSet<>(facilityReports.keySet()),
                        CcaBpmnProcessConstants.FACILITY_REPORTS, facilityReports,
                        CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_REQUEST_BUSINESS_KEY, uploadRequestBusinessKey))
                .build();

        startProcessRequestService.startProcess(requestParams);

        return taskPayload;
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_PROCESSING);
    }
}
