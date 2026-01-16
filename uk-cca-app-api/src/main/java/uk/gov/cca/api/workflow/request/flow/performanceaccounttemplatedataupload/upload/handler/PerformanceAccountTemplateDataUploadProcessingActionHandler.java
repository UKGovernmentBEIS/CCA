package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Component
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadProcessingActionHandler
		implements RequestTaskActionHandler<PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final PerformanceAccountTemplateDataUploadSubmitService performanceAccountTemplateDataUploadSubmitService;
	private final PerformanceAccountTemplateDataUploadValidationService performanceAccountTemplateDataUploadValidationService;
	private final PerformanceAccountTemplateDataGenerateCsvReportService performanceAccountTemplateDataGenerateCsvReportService;
	private final WorkflowService workflowService;
	private final StartProcessRequestService startProcessRequestService;

	@Override
	public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
			PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload requestTaskActionPayload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		final PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = (PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask
				.getPayload();
		
		final Request request = requestTask.getRequest();
		final PerformanceAccountTemplateDataUploadRequestPayload requestPayload = (PerformanceAccountTemplateDataUploadRequestPayload) request
				.getPayload();
		final SectorAssociationInfo sectorInfo = requestPayload.getSectorAssociationInfo();
		
		final int reportYear = 2024;
		final Year targetPeriodYear = Year.of(reportYear); //TODO make it configurable
		if(!requestTaskActionPayload.getPerformanceAccountTemplateDataUpload().getTargetPeriodType().equals(TargetPeriodType.TP6)) {
			throw new RuntimeException("cannot submit pat");
			//TODO remove me when configurable
		}
		
		//validate files
		FileReports fileReports = null;
		try {
			fileReports = performanceAccountTemplateDataUploadValidationService.extractValidateAndPersistFiles(
					requestTaskActionPayload.getPerformanceAccountTemplateDataUpload(), targetPeriodYear, sectorInfo,
					requestPayload.getSectorUserAssignee());
		} catch (ReportPackageMissingException e) {
			log.error(e.getMessage(), e);
			requestTaskPayload.setErrorType(PerformanceAccountTemplateDataUploadErrorType.REPORT_PACKAGE_MISSING);
			requestTaskPayload.setProcessingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
			return requestTaskPayload;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			requestTaskPayload.setErrorType(PerformanceAccountTemplateDataUploadErrorType.EXTRACT_VALIDATE_PERSIST_GENERIC_ERROR);
			requestTaskPayload.setProcessingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
			return requestTaskPayload;
		} 
		
		//submit request task payload
		performanceAccountTemplateDataUploadSubmitService.submitUpload(requestTask, requestTaskActionPayload, fileReports);
		
		if (fileReports.getAccountFileReports().isEmpty()
				|| fileReports.getAccountFileReports().values().stream().allMatch(report -> Boolean.FALSE.equals(report.getSucceeded()))) {
			//all files failed. generate csv file with the errors and return
			try {
				performanceAccountTemplateDataGenerateCsvReportService.generateCsvReport(requestTaskId, fileReports);
			} catch (Exception e) {
				log.error("CSV report file generation failed", e);
				requestTaskPayload.setErrorType(PerformanceAccountTemplateDataUploadErrorType.CSV_GENERATION_FAILED);
			} finally {
				requestTaskPayload.setProcessingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
			}
		} else {
			// trigger processing flow for valid files
			final Map<Long, AccountUploadReport> validAccountFileReports = fileReports.getAccountFileReports().entrySet().stream()
				    .filter(entry -> BooleanUtils.isNotFalse(entry.getValue().getSucceeded()))
				    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			
			final CcaRequestParams requestParams = CcaRequestParams.builder()
                    .type(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING)
                    .requestResources(Map.of(
                            CcaResourceType.SECTOR_ASSOCIATION, sectorInfo.getId().toString(),
                            ResourceType.CA, sectorInfo.getCompetentAuthority().name()
                    ))
                    .requestPayload(PerformanceAccountTemplateDataProcessingRequestPayload.builder()
                            .payloadType(CcaRequestPayloadType.PERFORMANCE_ACCCOUNT_TEMPLATE_DATA_PROCESSING_PAYLOAD)
                            .targetPeriodType(requestTaskActionPayload.getPerformanceAccountTemplateDataUpload().getTargetPeriodType())
                            .targetPeriodYear(targetPeriodYear)
                            .accountFileReports(validAccountFileReports)
                            .sectorAssociationInfo(sectorInfo)
                            .sectorUserAssignee(appUser.getUserId())
                            .build())
                    .processVars(Map.of(
                            // Wrap to hashset to be serializable (HashMap.Keyset is not serializable)
                            BpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(validAccountFileReports.keySet()),
                            CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS, validAccountFileReports,
                            CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0,
							CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_REQUEST_BUSINESS_KEY,
							(String) workflowService.getVariable(request.getProcessInstanceId(),
									BpmnProcessConstants.BUSINESS_KEY)))
                    .build();

            startProcessRequestService.startProcess(requestParams);
		}
		
		return requestTaskPayload;
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING);
	}

}
