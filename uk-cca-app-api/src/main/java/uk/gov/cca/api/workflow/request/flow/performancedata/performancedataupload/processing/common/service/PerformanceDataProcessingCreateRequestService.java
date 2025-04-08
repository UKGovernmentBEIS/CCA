package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Service
@RequiredArgsConstructor
public class PerformanceDataProcessingCreateRequestService {

	private final RequestService requestService;
	private final StartProcessRequestService startProcessRequestService;
	private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
	
	@Transactional
	public void createRequest(TargetUnitAccountUploadReport accountReport, String parentRequestId, String parentRequestBusinessKey) {	    
		final Request parentRequest = requestService.findRequestById(parentRequestId);
		final PerformanceDataProcessingRequestPayload parentRequestPayload =
				(PerformanceDataProcessingRequestPayload) parentRequest.getPayload();

		// Get next report version
		final int reportVersion = accountPerformanceDataStatusQueryService.getNextAccountPerformanceDataReportVersion(
				accountReport.getAccountId(), parentRequestPayload.getTargetPeriodDetails().getBusinessId());

		final RequestParams requestParams = RequestParams.builder()
				.type(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING)
				.requestResources(Map.of(
						ResourceType.ACCOUNT, accountReport.getAccountId().toString(),
						CcaResourceType.SECTOR_ASSOCIATION, parentRequestPayload.getSectorAssociationInfo().getId().toString()
				))
				.requestPayload(PerformanceDataSpreadsheetProcessingRequestPayload.builder()
						.payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_PAYLOAD)
						.sectorUserAssignee(parentRequestPayload.getSectorUserAssignee())
						.accountId(accountReport.getAccountId())
						.accountReportFile(accountReport.getFile())
						.build())
				.requestMetadata(PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
						.type(CcaRequestMetadataType.PERFORMANCE_DATA_PROCESSING)
						.parentRequestId(parentRequestId)
						.accountBusinessId(accountReport.getAccountBusinessId())
						.sectorAssociationInfo(parentRequestPayload.getSectorAssociationInfo())
						.targetPeriodDetails(parentRequestPayload.getTargetPeriodDetails())
						.performanceDataTargetPeriodType(parentRequestPayload.getPerformanceDataTargetPeriodType())
						.submissionType(parentRequestPayload.getSubmissionType())
						.reportVersion(reportVersion)
						.uploadedDate(parentRequestPayload.getUploadedDate())
						.build())
				.processVars(Map.of(
						BpmnProcessConstants.ACCOUNT_ID, accountReport.getAccountId(),
						CcaBpmnProcessConstants.PERFORMANCE_DATA_PROCESSING_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT, accountReport
				))
				.build();
		
		startProcessRequestService.startProcess(requestParams);
	}
}
