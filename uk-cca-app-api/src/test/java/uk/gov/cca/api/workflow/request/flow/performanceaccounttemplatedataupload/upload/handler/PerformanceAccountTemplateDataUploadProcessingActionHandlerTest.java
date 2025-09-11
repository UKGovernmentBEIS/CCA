package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Year;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.utils.PerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.PerformanceAccountTemplateDataGenerateCsvReportService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.PerformanceAccountTemplateDataUploadSubmitService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.PerformanceAccountTemplateDataUploadValidationService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.ReportPackageMissingException;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadProcessingActionHandlerTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadProcessingActionHandler cut;
	
	@Mock
	private RequestTaskService requestTaskService;
	
	@Mock
	private PerformanceAccountTemplateDataUploadSubmitService performanceAccountTemplateDataUploadSubmitService;
	
	@Mock
	private PerformanceAccountTemplateDataUploadValidationService performanceAccountTemplateDataUploadValidationService;
	
	@Mock
	private PerformanceAccountTemplateDataGenerateCsvReportService performanceAccountTemplateDataGenerateCsvReportService;
	
	@Mock
	private WorkflowService workflowService;
	
	@Mock
	private StartProcessRequestService startProcessRequestService;
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes())
				.containsExactly(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING);
	}
	
	@Test
	void process_empty_account_reports_csv_generation_fails() throws IOException, ReportPackageMissingException {
		Long requestTaskId = 1L;
		String requestTaskActionType = "reqActionType";
		AppUser appUser = AppUser.builder().userId("userId").build();
		
		PerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = PerformanceAccountTemplateDataUpload.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
		
		PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload requestTaskActionPayload = PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.builder()
				.performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
				.build();
		
		SectorAssociationInfo sectorInfo = SectorAssociationInfo.builder()
				.id(1L)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		
		PerformanceAccountTemplateDataUploadRequestPayload requestPayload = PerformanceAccountTemplateDataUploadRequestPayload.builder()
				.sectorAssociationInfo(sectorInfo)
				.sectorUserAssignee("sectorUser")
				.build();
		
		Request request = Request.builder()
				.processInstanceId("procInstanceId")
				.payload(requestPayload)
				.build();
		
		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.request(request)
				.payload(requestTaskPayload)
				.build();
		
		FileReports fileReports = FileReports.builder()
				.build();
		
		Year targetPeriodYear = Year.of(2024);
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		when(performanceAccountTemplateDataUploadValidationService
				.extractValidateAndPersistFiles(performanceAccountTemplateDataUpload, targetPeriodYear, sectorInfo, "sectorUser")).thenReturn(fileReports);
		doThrow(new IOException()).when(performanceAccountTemplateDataGenerateCsvReportService)
				.generateCsvReport(requestTaskId, fileReports);
		
		RequestTaskPayload result = cut.process(requestTaskId, requestTaskActionType, appUser, requestTaskActionPayload);
		
		assertThat(((PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) result).getErrorType())
				.isEqualTo(PerformanceAccountTemplateDataUploadErrorType.CSV_GENERATION_FAILED);
		assertThat(requestTaskPayload.getProcessingStatus()).isEqualTo(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(performanceAccountTemplateDataUploadValidationService, times(1)).extractValidateAndPersistFiles(performanceAccountTemplateDataUpload, targetPeriodYear, sectorInfo, "sectorUser");
		verify(performanceAccountTemplateDataUploadSubmitService, times(1)).submitUpload(requestTask, requestTaskActionPayload, fileReports);
		verify(performanceAccountTemplateDataGenerateCsvReportService, times(1)).generateCsvReport(requestTaskId, fileReports);
		verifyNoInteractions(startProcessRequestService);
	}
	
	@Test
	void process_empty_account_reports() throws IOException, ReportPackageMissingException {
		Long requestTaskId = 1L;
		String requestTaskActionType = "reqActionType";
		AppUser appUser = AppUser.builder().userId("userId").build();
		
		PerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = PerformanceAccountTemplateDataUpload.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
		
		PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload requestTaskActionPayload = PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.builder()
				.performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
				.build();
		
		SectorAssociationInfo sectorInfo = SectorAssociationInfo.builder()
				.id(1L)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		
		PerformanceAccountTemplateDataUploadRequestPayload requestPayload = PerformanceAccountTemplateDataUploadRequestPayload.builder()
				.sectorAssociationInfo(sectorInfo)
				.sectorUserAssignee("sectorUser")
				.build();
		
		Request request = Request.builder()
				.processInstanceId("procInstanceId")
				.payload(requestPayload)
				.build();
		
		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.request(request)
				.payload(requestTaskPayload)
				.build();
		
		FileReports fileReports = FileReports.builder()
				.build();
		Year targetPeriodYear = Year.of(2024);
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		when(performanceAccountTemplateDataUploadValidationService
				.extractValidateAndPersistFiles(performanceAccountTemplateDataUpload, targetPeriodYear, sectorInfo, "sectorUser")).thenReturn(fileReports);
		
		RequestTaskPayload result = cut.process(requestTaskId, requestTaskActionType, appUser, requestTaskActionPayload);
		
		assertThat(((PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) result).getErrorType())
		.isNull();
		
		assertThat(requestTaskPayload.getProcessingStatus()).isEqualTo(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(performanceAccountTemplateDataUploadValidationService, times(1)).extractValidateAndPersistFiles(performanceAccountTemplateDataUpload, targetPeriodYear, sectorInfo, "sectorUser");
		verify(performanceAccountTemplateDataUploadSubmitService, times(1)).submitUpload(requestTask, requestTaskActionPayload, fileReports);
		verify(performanceAccountTemplateDataGenerateCsvReportService, times(1)).generateCsvReport(requestTaskId, fileReports);
		verifyNoInteractions(startProcessRequestService);
	}
	
	@Test
	void process_not_empty_account_reports() throws ReportPackageMissingException {
		Long requestTaskId = 1L;
		String requestTaskActionType = "reqActionType";
		AppUser appUser = AppUser.builder().userId("userId").build();
		
		PerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = PerformanceAccountTemplateDataUpload.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
		
		PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload requestTaskActionPayload = PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.builder()
				.performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
				.build();
		
		SectorAssociationInfo sectorInfo = SectorAssociationInfo.builder()
				.id(1L)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		
		PerformanceAccountTemplateDataUploadRequestPayload requestPayload = PerformanceAccountTemplateDataUploadRequestPayload.builder()
				.sectorAssociationInfo(sectorInfo)
				.sectorUserAssignee("sectorUser")
				.build();
		
		Request request = Request.builder()
				.processInstanceId("procInstanceId")
				.payload(requestPayload)
				.build();
		
		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.request(request)
				.payload(requestTaskPayload)
				.build();
		
		FileReports fileReports = FileReports.builder()
				.accountFileReports(Map.of(
						1L, AccountUploadReport.builder().accountId(1L).succeeded(true).build(),
						2L, AccountUploadReport.builder().accountId(2L).succeeded(false).build()
						))
				.build();
		Year targetPeriodYear = Year.of(2024);
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		when(performanceAccountTemplateDataUploadValidationService
				.extractValidateAndPersistFiles(performanceAccountTemplateDataUpload, targetPeriodYear, sectorInfo, "sectorUser")).thenReturn(fileReports);
		when(workflowService.getVariable(request.getProcessInstanceId(), BpmnProcessConstants.BUSINESS_KEY)).thenReturn("pr");
		
		RequestTaskPayload result = cut.process(requestTaskId, requestTaskActionType, appUser, requestTaskActionPayload);
		
		assertThat(((PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) result).getErrorType())
		.isNull();
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(performanceAccountTemplateDataUploadValidationService, times(1)).extractValidateAndPersistFiles(performanceAccountTemplateDataUpload, targetPeriodYear, sectorInfo, "sectorUser");
		verify(performanceAccountTemplateDataUploadSubmitService, times(1)).submitUpload(requestTask, requestTaskActionPayload, fileReports);
		verifyNoInteractions(performanceAccountTemplateDataGenerateCsvReportService);
		verify(workflowService, times(1)).getVariable(request.getProcessInstanceId(), BpmnProcessConstants.BUSINESS_KEY);
		
		Map<Long, AccountUploadReport> validAccountFileReports = Map.of(
				1L, AccountUploadReport.builder().accountId(1L).succeeded(true).build()
				);
		verify(startProcessRequestService, times(1)).startProcess(CcaRequestParams.builder()
                    .type(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING)
                    .requestResources(Map.of(
                            CcaResourceType.SECTOR_ASSOCIATION, sectorInfo.getId().toString(),
                            ResourceType.CA, sectorInfo.getCompetentAuthority().name()
                    ))
                    .requestPayload(PerformanceAccountTemplateDataProcessingRequestPayload.builder()
                            .payloadType(CcaRequestPayloadType.PERFORMANCE_ACCCOUNT_TEMPLATE_DATA_PROCESSING_PAYLOAD)
                            .targetPeriodType(requestTaskActionPayload.getPerformanceAccountTemplateDataUpload().getTargetPeriodType())
                            .targetPeriodYear(Year.of(2024))
                            .accountFileReports(validAccountFileReports)
                            .sectorAssociationInfo(sectorInfo)
                            .sectorUserAssignee(appUser.getUserId())
                            .build())
                    .processVars(Map.of(
                            BpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(validAccountFileReports.keySet()),
                            CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS, validAccountFileReports,
                            CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0,
							CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_REQUEST_BUSINESS_KEY, "pr"))
                    .build());
	}
}
