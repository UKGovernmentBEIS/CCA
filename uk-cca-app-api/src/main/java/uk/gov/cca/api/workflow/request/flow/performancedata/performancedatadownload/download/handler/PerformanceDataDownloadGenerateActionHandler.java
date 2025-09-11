package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.utils.PerformanceDataUtility;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.validation.PerformanceDataDownloadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.service.PerformanceDataAccountQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataGenerateRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.documenttemplate.service.DocumentTemplateFileService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PerformanceDataDownloadGenerateActionHandler
        implements RequestTaskActionHandler<PerformanceDataGenerateRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final TargetPeriodService targetPeriodService;
    private final DocumentTemplateFileService documentTemplateFileService;
    private final WorkflowService workflowService;
    private final StartProcessRequestService startProcessRequestService;
    private final PerformanceDataAccountQueryService performanceDataAccountQueryService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                                      PerformanceDataGenerateRequestTaskActionPayload actionPayload) {
        // Update task payload
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PerformanceDataDownloadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setTargetPeriodType(actionPayload.getTargetPeriodType());
        taskPayload.setProcessCompleted(false);

        // Extract target period details
        TargetPeriodDTO targetPeriodDetails = targetPeriodService
                .getTargetPeriodByBusinessId(actionPayload.getTargetPeriodType().getReferenceTargetPeriod());

        // Get Primary / Secondary
        final PerformanceDataSubmissionType submissionType = PerformanceDataUtility
                .determinePerformanceDataSubmissionType(LocalDate.now(), targetPeriodDetails);

        // Get eligible accounts
        final SectorAssociationInfo sectorAssociation = taskPayload.getSectorAssociationInfo();

        List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(
                        sectorAssociation.getId(), actionPayload.getTargetPeriodType().getReferenceTargetPeriod());
        Set<Long> eligibleAccountIds = eligibleAccounts.stream()
                .map(TargetUnitAccountBusinessInfoDTO::getAccountId)
                .collect(Collectors.toSet());

        if (eligibleAccountIds.isEmpty()) {
            // If no accounts set process as completed
            taskPayload.setProcessCompleted(true);
            taskPayload.setErrorMessage(PerformanceDataDownloadViolation
                    .PerformanceDataDownloadViolationMessage.NO_ELIGIBLE_ACCOUNTS_FOR_TPR_REPORTING.name());
        }

        else {
            final Request request = requestTask.getRequest();
            final String downloadRequestBusinessKey = (String) workflowService
                    .getVariable(request.getProcessInstanceId(), BpmnProcessConstants.BUSINESS_KEY);

            // Get excel template
            TargetPeriodDocumentTemplate excelTemplate = TargetPeriodDocumentTemplate
                    .getTargetPeriodDocumentTemplate(actionPayload.getTargetPeriodType());
            FileDTO template = documentTemplateFileService
                    .getFileDocumentTemplateByTypeAndCompetentAuthority(excelTemplate.name(), sectorAssociation.getCompetentAuthority());

            // Create generate workflow
            CcaRequestParams requestParams = CcaRequestParams.builder()
                    .type(CcaRequestType.PERFORMANCE_DATA_GENERATE)
                    .requestResources(Map.of(
                            CcaResourceType.SECTOR_ASSOCIATION, sectorAssociation.getId().toString(),
                            ResourceType.CA, sectorAssociation.getCompetentAuthority().name()
                    ))
                    .requestPayload(PerformanceDataGenerateRequestPayload.builder()
                            .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_GENERATE_PAYLOAD)
                            .targetPeriodDocument(excelTemplate)
                            .template(template)
                            .sectorAssociationInfo(sectorAssociation)
                            .targetPeriodType(actionPayload.getTargetPeriodType())
                            .submissionType(submissionType)
                            .accountsReports(eligibleAccounts.stream()
                                    .collect(Collectors.toMap(
                                            TargetUnitAccountBusinessInfoDTO::getAccountId,
                                            acc -> TargetUnitAccountReport.builder()
                                                    .accountId(acc.getAccountId())
                                                    .accountBusinessId(acc.getBusinessId())
                                                    .build()))
                            )
                            .sectorUserAssignee(appUser.getUserId())
                            .build())
                    .processVars(Map.of(
                            BpmnProcessConstants.ACCOUNT_IDS, eligibleAccountIds,
                            CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED, 0,
                            CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_REQUEST_BUSINESS_KEY, downloadRequestBusinessKey
                    ))
                    .build();

            startProcessRequestService.startProcess(requestParams);
        }

        return taskPayload;
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_DOWNLOAD_GENERATE);
    }
}
