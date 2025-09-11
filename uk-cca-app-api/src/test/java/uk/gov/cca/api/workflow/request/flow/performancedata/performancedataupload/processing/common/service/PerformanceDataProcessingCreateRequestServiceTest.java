package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataProcessingCreateRequestServiceTest {

    @InjectMocks
    private PerformanceDataProcessingCreateRequestService performanceDataProcessingCreateRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final FileInfoDTO accountReportFile = FileInfoDTO.builder().name("excel").build();
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .accountId(accountId)
                .file(accountReportFile)
                .build();
        final String parentRequestId = "parentRequestId";
        final String parentRequestBusinessKey = "bk-parentRequestId";

        final Long sectorId = 11L;
        final String sectorUserAssignee = "sectorUserAssignee";
        final SectorAssociationInfo sectorAssociation = SectorAssociationInfo.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .id(sectorId)
                .build();
        final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
        final String performanceDataTemplateVersion = "6.0";
        final TargetPeriodDTO targetPeriodDTO = TargetPeriodDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .performanceDataTemplateVersion(performanceDataTemplateVersion)
                .secondaryReportingStartDate(LocalDate.of(2025, 5, 3))
                .build();
        final LocalDate uploadedDate = LocalDate.of(2025, 2, 3);
        final Request parentRequest = Request.builder()
                .id(parentRequestId)
                .payload(PerformanceDataProcessingRequestPayload.builder()
                        .sectorUserAssignee(sectorUserAssignee)
                        .targetPeriodDetails(targetPeriodDTO)
                        .sectorAssociationInfo(sectorAssociation)
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .submissionType(submissionType)
                        .uploadedDate(uploadedDate)
                        .build())
                .build();

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING)
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, accountId.toString(),
                        CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
                ))
                .requestPayload(PerformanceDataSpreadsheetProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_PAYLOAD)
                        .sectorUserAssignee(sectorUserAssignee)
                        .accountId(accountId)
                        .accountReportFile(accountReportFile)
                        .build())
                .requestMetadata(PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.PERFORMANCE_DATA_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(sectorAssociation)
                        .targetPeriodDetails(targetPeriodDTO)
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .submissionType(submissionType)
                        .reportVersion(2)
                        .uploadedDate(uploadedDate)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.PERFORMANCE_DATA_PROCESSING_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT, accountReport
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
        when(accountPerformanceDataStatusQueryService.getNextAccountPerformanceDataReportVersion(accountId, TargetPeriodType.TP6))
                .thenReturn(2);

        // Invoke
        performanceDataProcessingCreateRequestService.createRequest(accountReport, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getNextAccountPerformanceDataReportVersion(accountId, TargetPeriodType.TP6);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
