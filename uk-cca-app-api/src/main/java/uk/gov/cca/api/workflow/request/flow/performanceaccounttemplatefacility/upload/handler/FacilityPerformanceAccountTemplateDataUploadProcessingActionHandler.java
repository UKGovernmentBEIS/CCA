package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service.FacilityPerformanceAccountTemplateDataUploadService;
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
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadProcessingActionHandler implements RequestTaskActionHandler<FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final StartProcessRequestService startProcessRequestService;
    private final FacilityPerformanceAccountTemplateDataUploadService performanceAccountTemplateDataUploadService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload taskActionPayload) {
        //TODO:  enhance logic

        final LocalDateTime submissionDate = LocalDateTime.now();
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        // Process data and validate
        performanceAccountTemplateDataUploadService.process(requestTask, taskActionPayload, submissionDate);

        // Create processing workflow
        final SectorAssociationInfo sectorAssociation = requestTaskPayload.getSectorAssociationInfo();
        final Year targetYear = requestTaskPayload.getPerformanceAccountTemplateDataUpload().getTargetYear();
        Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = requestTaskPayload.getFacilityReports();
        final String uploadRequestBusinessKey = (String) workflowService
                .getVariable(request.getProcessInstanceId(), BpmnProcessConstants.BUSINESS_KEY);

        //TODO: remove after testing
//        facilityReports.put(3L, FacilityPerformanceAccountTemplateUploadReport.builder()
//                .facilityId(3L)
//                .facilityBusinessId("ADS_1-F00007")
//                .accountId(5L)
//                .build());

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, sectorAssociation.getId().toString(),
                        ResourceType.CA, sectorAssociation.getCompetentAuthority().name()
                ))
                .requestPayload(FacilityPerformanceAccountTemplateDataProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING_PAYLOAD)
                        .parentRequestId(request.getId())
                        .sectorAssociationInfo(sectorAssociation)
                        .sectorUserAssignee(appUser.getUserId())
                        .targetYear(targetYear)
                        .submissionDate(submissionDate)
                        .build())
                .processVars(Map.of(
                        // Wrap to hashset to be serializable (HashMap.Keyset is not serializable)
                        CcaBpmnProcessConstants.FACILITY_IDS, new HashSet<>(facilityReports.keySet()),
                        CcaBpmnProcessConstants.FACILITY_REPORTS, facilityReports,
                        CcaBpmnProcessConstants.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_REQUEST_BUSINESS_KEY, uploadRequestBusinessKey))
                .build();

        startProcessRequestService.startProcess(requestParams);

        return requestTaskPayload;
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING);
    }
}
