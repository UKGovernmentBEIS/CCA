package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.handler;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.utils.PerformanceDataUtility;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.service.PerformanceDataAccountQueryService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadCompletedService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadService;
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

@Component
@RequiredArgsConstructor
public class PerformanceDataUploadProcessingActionHandler implements
        RequestTaskActionHandler<PerformanceDataUploadProcessingRequestTaskActionPayload> {

    private final PerformanceDataAccountQueryService performanceDataAccountQueryService;
    private final PerformanceDataUploadService performanceDataUploadService;
    private final PerformanceDataUploadCompletedService performanceDataUploadCompletedService;
    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;
    private final StartProcessRequestService startProcessRequestService;
    private final TargetPeriodService targetPeriodService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                                      PerformanceDataUploadProcessingRequestTaskActionPayload actionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setProcessCompleted(false);
        final SectorAssociationInfo sectorAssociation = taskPayload.getSectorAssociationInfo();

        // Extract target period details
        TargetPeriodDTO targetPeriodDetails = targetPeriodService
                .getTargetPeriodByBusinessId(actionPayload.getPerformanceDataUpload().getPerformanceDataTargetPeriodType().getReferenceTargetPeriod());

        // Get current date of process initiated for all accounts
        final LocalDate initiatedProcessDate = LocalDate.now();

        // Get Primary / Secondary
        final PerformanceDataSubmissionType submissionType = PerformanceDataUtility
                .determinePerformanceDataSubmissionType(initiatedProcessDate, targetPeriodDetails);

        // Get active accounts
        List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(
                        sectorAssociation.getId(), targetPeriodDetails.getBusinessId(), submissionType);

        // Build account reports from packages
        final Map<Long, TargetUnitAccountUploadReport> accountReports = performanceDataUploadService.submit(requestTask,
                actionPayload.getPerformanceDataUpload(), eligibleAccounts);

        if (accountReports.isEmpty()) {
            // If no accounts finalize task
            performanceDataUploadCompletedService.completedDueToEmptyAccountReports(requestTask);
        } else {
            // Create processing workflow
            final String uploadRequestBusinessKey = (String) workflowService
                    .getVariable(request.getProcessInstanceId(), BpmnProcessConstants.BUSINESS_KEY);

            final CcaRequestParams requestParams = CcaRequestParams.builder()
                    .type(CcaRequestType.PERFORMANCE_DATA_PROCESSING)
                    .requestResources(Map.of(
                            CcaResourceType.SECTOR_ASSOCIATION, sectorAssociation.getId().toString(),
                            ResourceType.CA, sectorAssociation.getCompetentAuthority().name()
                    ))
                    .requestPayload(PerformanceDataProcessingRequestPayload.builder()
                            .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_PROCESSING_PAYLOAD)
                            .performanceDataTargetPeriodType(actionPayload.getPerformanceDataUpload().getPerformanceDataTargetPeriodType())
                            .sectorAssociationInfo(sectorAssociation)
                            .sectorUserAssignee(appUser.getUserId())
                            .targetPeriodDetails(targetPeriodDetails)
                            .submissionType(submissionType)
                            .uploadedDate(initiatedProcessDate)
                            .build())
                    .processVars(Map.of(
                            // Wrap to hashset to be serializable (HashMap.Keyset is not serializable)
                            BpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountReports.keySet()),
                            CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS, accountReports,
                            CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_NUMBER_OF_ACCOUNTS_COMPLETED, 0,
                            CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_REQUEST_BUSINESS_KEY, uploadRequestBusinessKey))
                    .build();

            startProcessRequestService.startProcess(requestParams);
        }

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_PROCESSING);
    }
}
