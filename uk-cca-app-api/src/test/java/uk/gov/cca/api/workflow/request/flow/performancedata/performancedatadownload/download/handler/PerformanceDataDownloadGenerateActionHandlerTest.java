package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.handler;

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
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.validation.PerformanceDataDownloadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataGenerateRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.service.PerformanceDataAccountQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.documenttemplate.service.DocumentTemplateFileService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadGenerateActionHandlerTest {

    @InjectMocks
    private PerformanceDataDownloadGenerateActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private DocumentTemplateFileService documentTemplateFileService;

    @Mock
    private PerformanceDataAccountQueryService performanceDataAccountQueryService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_DOWNLOAD_GENERATE;
        final AppUser appUser = AppUser.builder().userId("sector").build();
        final PerformanceDataGenerateRequestTaskActionPayload actionPayload =
                PerformanceDataGenerateRequestTaskActionPayload.builder()
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .build();
        final long sectorAssociationId = 2L;
        final String downloadRequestBusinessKey = "bk-request";
        final String processInstanceId = "proc-instance-id";

        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .id(sectorAssociationId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        final FileDTO template = FileDTO.builder().fileName("template").build();
        final TargetPeriodYearDTO targetPeriodDTO = TargetPeriodYearDTO.builder()
                .secondaryReportingStartDate(LocalDate.of(2025, 5, 2))
                .performanceDataTemplateVersion("6.0")
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .processInstanceId(processInstanceId)
                        .build())
                .payload(PerformanceDataDownloadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .build())
                .build();

        final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(11L).businessId("bk-11").build(),
                TargetUnitAccountBusinessInfoDTO.builder().accountId(12L).businessId("bk-12").build()
        );

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(targetPeriodService
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear()))
                .thenReturn(targetPeriodDTO);
        when(documentTemplateFileService.getFileDocumentTemplateByTypeAndCompetentAuthority(
                TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6.name(), CompetentAuthorityEnum.ENGLAND)).thenReturn(template);
        when(workflowService.getVariable(processInstanceId, BpmnProcessConstants.BUSINESS_KEY))
                .thenReturn(downloadRequestBusinessKey);
        when(performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorAssociationId, TargetPeriodType.TP6))
                .thenReturn(eligibleAccounts);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        // Verify
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload()).getTargetPeriodType())
                .isEqualTo(PerformanceDataTargetPeriodType.TP6);
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload()).getProcessCompleted())
                .isFalse();
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload()).getErrorMessage())
                .isNull();
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(targetPeriodService, times(1))
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear());
        verify(workflowService, times(1))
                .getVariable(processInstanceId, BpmnProcessConstants.BUSINESS_KEY);
        verify(performanceDataAccountQueryService, times(1))
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorAssociationId, TargetPeriodType.TP6);
        verify(startProcessRequestService, times(1))
                .startProcess(any());
    }

    @Test
    void process_empty_accounts() {
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_DOWNLOAD_GENERATE;
        final AppUser appUser = AppUser.builder().userId("sector").build();
        final PerformanceDataGenerateRequestTaskActionPayload actionPayload =
                PerformanceDataGenerateRequestTaskActionPayload.builder()
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .build();
        final long sectorAssociationId = 2L;

        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .id(sectorAssociationId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        final TargetPeriodYearDTO targetPeriodDTO = TargetPeriodYearDTO.builder()
                .secondaryReportingStartDate(LocalDate.of(2025, 5, 2))
                .performanceDataTemplateVersion("6.0")
                .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataDownloadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(targetPeriodService
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear()))
                .thenReturn(targetPeriodDTO);
        when(performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorAssociationId, TargetPeriodType.TP6))
                .thenReturn(List.of());

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        // Verify
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload()).getTargetPeriodType())
                .isEqualTo(PerformanceDataTargetPeriodType.TP6);
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload()).getProcessCompleted())
                .isTrue();
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload()).getErrorMessage())
                .isEqualTo(PerformanceDataDownloadViolation
                        .PerformanceDataDownloadViolationMessage.NO_ELIGIBLE_ACCOUNTS_FOR_TPR_REPORTING.name());
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(targetPeriodService, times(1))
                .getTargetPeriodByTargetPeriodTypeAndTargetYear(PerformanceDataTargetPeriodType.TP6.getReferenceTargetPeriod(), PerformanceDataTargetPeriodType.TP6.getTargetYear());
        verify(performanceDataAccountQueryService, times(1))
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorAssociationId, TargetPeriodType.TP6);
        verifyNoInteractions(documentTemplateFileService, workflowService, startProcessRequestService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_DOWNLOAD_GENERATE);
    }
}
