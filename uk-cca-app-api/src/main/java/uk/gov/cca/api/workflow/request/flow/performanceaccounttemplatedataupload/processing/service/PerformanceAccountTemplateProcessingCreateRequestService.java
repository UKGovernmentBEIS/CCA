package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingCreateRequestService {

	private final RequestService requestService;
	private final StartProcessRequestService startProcessRequestService;
	private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
	
	@Transactional
	public void createRequest(AccountUploadReport accountReport, String parentRequestId,
			String parentRequestBusinessKey) {
		final Request parentRequest = requestService.findRequestById(parentRequestId);
		final PerformanceAccountTemplateDataProcessingRequestPayload parentRequestPayload =
				(PerformanceAccountTemplateDataProcessingRequestPayload) parentRequest.getPayload();
		
		int nextReportVersion = 1; //TODO find next report version by accountReport.accountId and parentRequestPayload.targetPeriodYear
		
		final RequestParams requestParams = RequestParams.builder()
				.type(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
				.requestResources(requestCreateAccountAndSectorResourcesService
						.createRequestResources(accountReport.getAccountId()))
				.requestMetadata(PerformanceAccountTemplateProcessingRequestMetadata.builder()
						.type(CcaRequestMetadataType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
						.sectorAssociationInfo(parentRequestPayload.getSectorAssociationInfo())
						.accountBusinessId(accountReport.getAccountBusinessId())
						.targetPeriodType(parentRequestPayload.getTargetPeriodType())
						.targetPeriodYear(parentRequestPayload.getTargetPeriodYear())
						.reportVersion(nextReportVersion)
						.parentRequestId(parentRequestId)
						.build())
				.processVars(Map.of(
						CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountReport.getAccountId(),
						CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT, accountReport
				))
				.build();
		
		startProcessRequestService.startProcess(requestParams);
	}
}
