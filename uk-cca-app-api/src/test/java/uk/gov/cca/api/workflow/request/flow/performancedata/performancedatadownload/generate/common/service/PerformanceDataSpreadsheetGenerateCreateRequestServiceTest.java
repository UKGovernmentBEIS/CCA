package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetGenerateCreateRequestServiceTest {

    @InjectMocks
    private PerformanceDataSpreadsheetGenerateCreateRequestService performanceDataSpreadsheetGenerateCreateRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final long accountId = 1L;
        final String parentRequestId = "parent-request-id";
        final String parentRequestBusinessKey = "bk-parent-request-id";
        final String accountBusinessId = "bk-1";
        final String sectorUserAssignee = "sector";

        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .id(2L)
                .acronym("acronym")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        final FileDTO templateFileDTO = FileDTO.builder().fileName("excel").build();
        final Request request = Request.builder()
                .id(parentRequestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .sectorUserAssignee(sectorUserAssignee)
                        .targetPeriodDocument(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6)
                        .template(templateFileDTO)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .accountsReports(Map.of(
                                accountId,
                                TargetUnitAccountReport.builder()
                                        .accountId(accountId)
                                        .accountBusinessId(accountBusinessId)
                                        .build())
                        )
                        .build())
                .build();
        final UnderlyingAgreementDTO underlyingAgreement = UnderlyingAgreementDTO.builder().id(10L).build();
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = TargetUnitAccountDetailsDTO.builder().id(15L).build();
        final int reportVersion = 1;
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_GENERATE)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, "2",
                        ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
                ))
                .requestPayload(PerformanceDataSpreadsheetGenerateRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_SPREADSHEET_GENERATE_PAYLOAD)
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .requestMetadata(PerformanceDataSpreadsheetGenerateRequestMetadata.builder()
                        .type(CcaRequestMetadataType.PERFORMANCE_DATA_GENERATE)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountBusinessId)
                        .targetPeriodDocument(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6)
                        .template(templateFileDTO)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .underlyingAgreement(underlyingAgreement)
                        .targetUnitAccountDetails(targetUnitAccountDetails)
                        .reportVersion(reportVersion)
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

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreement);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(targetUnitAccountDetails);
        when(accountPerformanceDataStatusQueryService.getNextAccountPerformanceDataReportVersion(accountId, TargetPeriodType.TP6))
                .thenReturn(reportVersion);

        // Invoke
        performanceDataSpreadsheetGenerateCreateRequestService.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getNextAccountPerformanceDataReportVersion(accountId, TargetPeriodType.TP6);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
