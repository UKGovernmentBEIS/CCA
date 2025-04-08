package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetGenerateCreateRequestService {

	private final RequestService requestService;
	private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
	private final AccountReferenceDetailsService accountReferenceDetailsService;
	private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
	private final StartProcessRequestService startProcessRequestService;
	
	@Transactional
	public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
		final Request parentRequest = requestService.findRequestById(parentRequestId);
		final PerformanceDataGenerateRequestPayload parentRequestPayload =
				(PerformanceDataGenerateRequestPayload) parentRequest.getPayload();
		final String accountBusinessId = parentRequestPayload.getAccountsReports().get(accountId).getAccountBusinessId();

		final UnderlyingAgreementDTO underlyingAgreement = underlyingAgreementQueryService
				.getUnderlyingAgreementByAccountId(accountId);
		final TargetUnitAccountDetailsDTO targetUnitAccountDetails = accountReferenceDetailsService
				.getTargetUnitAccountDetails(accountId);
		final int reportVersion = accountPerformanceDataStatusQueryService
				.getNextAccountPerformanceDataReportVersion(accountId, parentRequestPayload.getTargetPeriodType().getReferenceTargetPeriod());
		final PerformanceDataContainer lastUploadedReport = accountPerformanceDataStatusQueryService
				.getLastUploadedReport(accountId, parentRequestPayload.getTargetPeriodType().getReferenceTargetPeriod())
				.orElse(null);

		final RequestParams requestParams = RequestParams.builder()
				.type(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_GENERATE)
				.requestResources(Map.of(
						CcaResourceType.SECTOR_ASSOCIATION, parentRequestPayload.getSectorAssociationInfo().getId().toString(),
						ResourceType.CA, parentRequestPayload.getSectorAssociationInfo().getCompetentAuthority().name()
				))
				.requestPayload(PerformanceDataSpreadsheetGenerateRequestPayload.builder()
						.payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_SPREADSHEET_GENERATE_PAYLOAD)
						.sectorUserAssignee(parentRequestPayload.getSectorUserAssignee())
						.build())
				.requestMetadata(PerformanceDataSpreadsheetGenerateRequestMetadata.builder()
						.type(CcaRequestMetadataType.PERFORMANCE_DATA_GENERATE)
						.parentRequestId(parentRequestId)
						.accountBusinessId(accountBusinessId)
						.targetPeriodDocument(parentRequestPayload.getTargetPeriodDocument())
						.template(parentRequestPayload.getTemplate())
						.sectorAssociationInfo(parentRequestPayload.getSectorAssociationInfo())
						.targetPeriodType(parentRequestPayload.getTargetPeriodType())
						.underlyingAgreement(underlyingAgreement)
						.targetUnitAccountDetails(targetUnitAccountDetails)
						.reportVersion(reportVersion)
						.submissionType(parentRequestPayload.getSubmissionType())
						.lastUploadedReport(lastUploadedReport)
						.build())
				.processVars(Map.of(
						CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountId,
						CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT, TargetUnitAccountReport.builder()
								.accountId(accountId)
								.accountBusinessId(accountBusinessId)
								.build()
				))
				.build();
		
		startProcessRequestService.startProcess(requestParams);
	}
}
