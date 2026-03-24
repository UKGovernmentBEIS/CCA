package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.service.PerformanceDataAccountQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUpload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadCompletedService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadProcessingActionHandlerTest {

    @InjectMocks
    private PerformanceDataUploadProcessingActionHandler handler;

    @Mock
    private PerformanceDataAccountQueryService performanceDataAccountQueryService;

    @Mock
    private PerformanceDataUploadService performanceDataUploadService;

    @Mock
    private PerformanceDataUploadCompletedService performanceDataUploadCompletedService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final Long sectorId = 11L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_PROCESSING;
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .build();
        final PerformanceDataUploadProcessingRequestTaskActionPayload actionPayload =
                PerformanceDataUploadProcessingRequestTaskActionPayload.builder()
                        .performanceDataUpload(performanceDataUpload)
                        .build();
        final String sectorUser = "sectorUser";
        final AppUser appUser = AppUser.builder().userId(sectorUser).build();
        final SectorAssociationInfo sectorAssociation = SectorAssociationInfo.builder()
                .id(sectorId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        final String processId = "processId";
        final TargetPeriodYearDTO targetPeriodDTO = TargetPeriodYearDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .secondaryReportingStartDate(LocalDate.of(2025, 5, 2))
                .performanceDataTemplateVersion("6.0")
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .processInstanceId(processId)
                        .build())
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociation)
                        .build())
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> activeAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).build()
        );
        final Map<Long, TargetUnitAccountUploadReport> accountReportsFiles = Map.of(1L,
                TargetUnitAccountUploadReport.builder()
                        .accountId(1L)
                        .file(FileInfoDTO.builder().name("excel").build())
                        .build()
                );
        final String uploadRequestBusinessKey = "bk-uploadRequestBusinessKey";

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorId, TargetPeriodType.TP6))
                .thenReturn(activeAccounts);
        when(performanceDataUploadService.submit(requestTask, performanceDataUpload, activeAccounts)).thenReturn(accountReportsFiles);
        when(targetPeriodService
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear()))
                .thenReturn(targetPeriodDTO);
        when(workflowService.getVariable(processId, BpmnProcessConstants.BUSINESS_KEY)).thenReturn(uploadRequestBusinessKey);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        // Verify
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getProcessCompleted())
                .isFalse();
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataAccountQueryService, times(1))
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorId, TargetPeriodType.TP6);
        verify(performanceDataUploadService, times(1))
                .submit(requestTask, performanceDataUpload, activeAccounts);
        verify(targetPeriodService, times(1))
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear());
        verify(workflowService, times(1)).getVariable(processId, BpmnProcessConstants.BUSINESS_KEY);
        verify(startProcessRequestService, times(1)).startProcess(any());
        verifyNoInteractions(performanceDataUploadCompletedService);
    }

    @Test
    void process_empty_accounts() {
        final Long requestTaskId = 1L;
        final Long sectorId = 11L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_PROCESSING;
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .build();
        final PerformanceDataUploadProcessingRequestTaskActionPayload actionPayload =
                PerformanceDataUploadProcessingRequestTaskActionPayload.builder()
                        .performanceDataUpload(performanceDataUpload)
                        .build();
        final AppUser appUser = AppUser.builder().build();
        final SectorAssociationInfo sectorAssociation = SectorAssociationInfo.builder()
                .id(sectorId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociation)
                        .build())
                .build();
        final TargetPeriodYearDTO targetPeriodDTO = TargetPeriodYearDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .secondaryReportingStartDate(LocalDate.of(2025, 5, 2))
                .performanceDataTemplateVersion("6.0")
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorId, TargetPeriodType.TP6))
                .thenReturn(List.of());
        when(targetPeriodService
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear()))
                .thenReturn(targetPeriodDTO);
        when(performanceDataUploadService.submit(requestTask, performanceDataUpload, List.of())).thenReturn(Map.of());

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        // Verify
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getProcessCompleted())
                .isFalse();
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataAccountQueryService, times(1))
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorId, TargetPeriodType.TP6);
        verify(targetPeriodService, times(1))
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear());
        verify(performanceDataUploadService, times(1))
                .submit(requestTask, performanceDataUpload, List.of());
        verify(performanceDataUploadCompletedService, times(1))
                .completedDueToEmptyAccountReports(requestTask);
        verifyNoInteractions(workflowService, startProcessRequestService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_PROCESSING);
    }
}
