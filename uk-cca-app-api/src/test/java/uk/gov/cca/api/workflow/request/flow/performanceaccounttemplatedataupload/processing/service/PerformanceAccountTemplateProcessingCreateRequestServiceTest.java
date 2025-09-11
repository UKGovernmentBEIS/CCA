package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingCreateRequestServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateProcessingCreateRequestService cut;
	
	@Mock
	private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
	
	@Mock
	private RequestService requestService;
	
	@Mock
	private PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;
	
	@Mock
	private StartProcessRequestService startProcessRequestService;
	
	@Test
	void createRequest() {
		AccountUploadReport accountReport = AccountUploadReport.builder()
				.accountId(1L)
				.accountBusinessId("1LBusiness")
				.build();
		
		String parentRequestId = "parRequestId";
		String parentRequestBusinessKey = "pareReKey";
		
		SectorAssociationInfo sectorInfo = SectorAssociationInfo.builder()
				.id(1L)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		PerformanceAccountTemplateDataProcessingRequestPayload parentRequestPayload = PerformanceAccountTemplateDataProcessingRequestPayload
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.sectorAssociationInfo(sectorInfo)
				.sectorUserAssignee("SecUAssingee")
				.targetPeriodYear(Year.of(2025))
				.build();
		
		Request parentRequest = Request.builder()
				.payload(parentRequestPayload)
				.build();
		
		when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
		when(performanceAccountTemplateDataQueryService.calculateNextReportVersion(accountReport.getAccountId(),
				parentRequestPayload.getTargetPeriodYear())).thenReturn(1);
		when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountReport.getAccountId()))
			.thenReturn(Map.of(
						ResourceType.ACCOUNT, accountReport.getAccountId().toString(),
						CcaResourceType.SECTOR_ASSOCIATION, parentRequestPayload.getSectorAssociationInfo().getId().toString()
				));
		
		cut.createRequest(accountReport, parentRequestId, parentRequestBusinessKey);
		
		verify(requestService, times(1)).findRequestById(parentRequestId);
		verify(performanceAccountTemplateDataQueryService, times(1)).calculateNextReportVersion(accountReport.getAccountId(),
				parentRequestPayload.getTargetPeriodYear());
		verify(requestCreateAccountAndSectorResourcesService, times(1)).createRequestResources(accountReport.getAccountId());
		verify(startProcessRequestService, times(1)).startProcess(RequestParams.builder()
				.type(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
				.requestResources(Map.of(
						ResourceType.ACCOUNT, accountReport.getAccountId().toString(),
						CcaResourceType.SECTOR_ASSOCIATION, parentRequestPayload.getSectorAssociationInfo().getId().toString()
				))
				.requestMetadata(PerformanceAccountTemplateProcessingRequestMetadata.builder()
						.type(CcaRequestMetadataType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
						.sectorAssociationInfo(parentRequestPayload.getSectorAssociationInfo())
						.sectorUserAssignee(parentRequestPayload.getSectorUserAssignee())
						.accountId(accountReport.getAccountId())
						.accountBusinessId(accountReport.getAccountBusinessId())
						.targetPeriodType(parentRequestPayload.getTargetPeriodType())
						.targetPeriodYear(parentRequestPayload.getTargetPeriodYear())
						.reportVersion(1)
						.parentRequestId(parentRequestId)
						.build())
				.processVars(Map.of(
						CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountReport.getAccountId(),
						CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT, accountReport
				))
				.build());
	}
}
