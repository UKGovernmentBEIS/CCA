package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.service.TP6PerformanceDataSpreadsheetProcessingExtractDataService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation.TP6PerformanceDataSpreadsheetTemplateValidator;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetProcessingServiceTest {

    private PerformanceDataSpreadsheetProcessingService performanceDataSpreadsheetProcessingService;

    @Mock
    private RequestService requestService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private TP6PerformanceDataSpreadsheetProcessingExtractDataService tp6PerformanceDataSpreadsheetProcessingExtractDataService;

    @Mock
    private TP6PerformanceDataSpreadsheetTemplateValidator tp6PerformanceDataSpreadsheetTemplateValidator;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;
    
    @Mock
    private AccountPerformanceDataStatusService accountPerformanceDataStatusService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @BeforeEach
    void setUp() {
        this.performanceDataSpreadsheetProcessingService = new PerformanceDataSpreadsheetProcessingService(
                this.requestService,
                this.fileAttachmentService,
                List.of(tp6PerformanceDataSpreadsheetProcessingExtractDataService),
                List.of(tp6PerformanceDataSpreadsheetTemplateValidator),
                this.underlyingAgreementQueryService,
                this.accountReferenceDetailsService,
                this.accountPerformanceDataStatusService,
                this.accountPerformanceDataStatusQueryService
        );
    }

    @Test
    void doProcess() throws Exception {
        final String requestId = "requestId";
        final String sectorUserAssignee = "sectorUserAssignee";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .errors(new ArrayList<>())
                .build();
        final String fileUUid = "fileUUid";
        final long accountId = 1L;
        final int reportVersion = 2;
        final PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .targetPeriodDetails(TargetPeriodDTO.builder()
                                .businessId(TargetPeriodType.TP6)
                                .build())
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .reportVersion(reportVersion)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .build();
        final FileInfoDTO accountFile = FileInfoDTO.builder().name("account1.xlsx").uuid(fileUUid).build();
        final Request request = Request.builder()
                .metadata(metadata)
                .payload(PerformanceDataSpreadsheetProcessingRequestPayload.builder()
                        .accountReportFile(accountFile)
                        .accountId(accountId)
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .build();
        final FileDTO accountReportFile = FileDTO.builder()
                .fileName("account1.xlsx")
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .build();
        final PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics = PerformanceDataCalculatedMetrics.builder().build();
        final TargetPeriodDocumentTemplate templateType = TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6;
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().id(accountId).build();
        final UnderlyingAgreementDTO underlyingAgreement = UnderlyingAgreementDTO.builder().accountId(accountId).build();
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(accountDetails)
                .underlyingAgreement(underlyingAgreement)
                .targetPeriodDetails(TargetPeriodDTO.builder()
                        .businessId(TargetPeriodType.TP6)
                        .build())
                .performanceDataCalculatedMetrics(performanceDataCalculatedMetrics)
                .reportVersion(reportVersion)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .fileName("account1.xlsx")
                .build();
        final PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload actionPayload =
                PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED_PAYLOAD)
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .accountReportFile(accountFile)
                        .performanceData(performanceData)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(fileAttachmentService.getFileDTO(fileUUid)).thenReturn(accountReportFile);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.getDocumentTemplateType())
                .thenReturn(templateType);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.getDocumentTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractData(metadata, accountReportFile))
                .thenReturn(performanceData);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.validateData(performanceData))
                .thenReturn(BusinessValidationResult.valid());
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractCalculatedData(performanceData))
                .thenReturn(performanceDataCalculatedMetrics);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId)).thenReturn(underlyingAgreement);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.validateBusinessData(referenceDetails, performanceData))
                .thenReturn(List.of(BusinessValidationResult.valid()));

        // Invoke
        performanceDataSpreadsheetProcessingService.doProcess(requestId, accountReport);

        // Verify
        assertThat(((PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceData);
        assertThat(((PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload()).getPerformanceDataCalculatedMetrics())
                .isEqualTo(performanceDataCalculatedMetrics);
        assertThat(accountReport.getPerformanceDataCalculatedMetrics()).isEqualTo(performanceDataCalculatedMetrics);
        assertThat(accountReport.getErrors()).isEmpty();
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(fileAttachmentService, times(1)).getFileDTO(fileUUid);
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .extractData(metadata, accountReportFile);
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .validateData(performanceData);
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .extractCalculatedData(performanceData);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementByAccountId(accountId);
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .validateBusinessData(referenceDetails, performanceData);
        verify(accountPerformanceDataStatusService, times(1)).submitAccountPerformanceData(
                any(PerformanceDataContainer.class), eq(accountId), eq(TargetPeriodType.TP6), eq(reportVersion),
                eq(PerformanceDataSubmissionType.PRIMARY));
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED, sectorUserAssignee);
    }

    @Test
    void doProcess_with_validation_errors() throws Exception {
        final String requestId = "requestId";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .errors(new ArrayList<>())
                .build();
        final String fileUUid = "fileUUid";
        final long accountId = 1L;
        final int reportVersion = 2;
        final PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .reportVersion(reportVersion)
                        .build();
        final Request request = Request.builder()
                .metadata(metadata)
                .payload(PerformanceDataSpreadsheetProcessingRequestPayload.builder()
                        .accountReportFile(FileInfoDTO.builder().name("account1.xlsx").uuid(fileUUid).build())
                        .accountId(accountId)
                        .build())
                .build();
        final FileDTO accountReportFile = FileDTO.builder()
                .fileName("account1.xlsx")
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .build();
        final PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics = PerformanceDataCalculatedMetrics.builder().build();
        final TargetPeriodDocumentTemplate templateType = TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6;
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().id(accountId).build();
        final UnderlyingAgreementDTO underlyingAgreement = UnderlyingAgreementDTO.builder().accountId(accountId).build();
        final PerformanceDataReferenceDetails referenceDetails = PerformanceDataReferenceDetails.builder()
                .accountDetails(accountDetails)
                .underlyingAgreement(underlyingAgreement)
                .performanceDataCalculatedMetrics(performanceDataCalculatedMetrics)
                .reportVersion(reportVersion)
                .fileName("account1.xlsx")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(fileAttachmentService.getFileDTO(fileUUid)).thenReturn(accountReportFile);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.getDocumentTemplateType())
                .thenReturn(templateType);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.getDocumentTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractData(metadata, accountReportFile))
                .thenReturn(performanceData);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.validateData(performanceData))
                .thenReturn(BusinessValidationResult.valid());
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractCalculatedData(performanceData))
                .thenReturn(performanceDataCalculatedMetrics);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId)).thenReturn(underlyingAgreement);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.validateBusinessData(referenceDetails, performanceData))
                .thenReturn(List.of(BusinessValidationResult.builder()
                        .valid(false)
                        .violations(List.of(
                                new PerformanceDataUploadViolation("sectionName", PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA, "message1", "message2"),
                                new PerformanceDataUploadViolation("sectionName", PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA, "message3")
                        ))
                        .build(),
                        BusinessValidationResult.builder()
                                .valid(false)
                                .violations(List.of(
                                        new PerformanceDataUploadViolation("sectionName2", PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA, "message4", "message5"),
                                        new PerformanceDataUploadViolation("sectionName2", PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA, "message6")
                                ))
                                .build()
                ));

        // Invoke
        performanceDataSpreadsheetProcessingService.doProcess(requestId, accountReport);

        // Verify
        assertThat(((PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceData);
        assertThat(((PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload()).getPerformanceDataCalculatedMetrics())
                .isEqualTo(performanceDataCalculatedMetrics);
        assertThat(accountReport.getPerformanceDataCalculatedMetrics()).isEqualTo(performanceDataCalculatedMetrics);
        assertThat(accountReport.getErrors())
                .containsExactly("message1", "message2", "message3", "message4", "message5", "message6");
        verify(requestService, times(1)).findRequestById(requestId);
        verify(fileAttachmentService, times(1)).getFileDTO(fileUUid);
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .extractData(metadata, accountReportFile);
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .validateData(performanceData);
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .extractCalculatedData(performanceData);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementByAccountId(accountId);
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .validateBusinessData(referenceDetails, performanceData);
        verifyNoInteractions(accountPerformanceDataStatusService);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void doProcess_data_validation_not_empty() throws Exception {
        final String requestId = "requestId";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .errors(new ArrayList<>())
                .build();
        final String fileUUid = "fileUUid";
        final long accountId = 1L;
        final int reportVersion = 2;
        final PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .reportVersion(reportVersion)
                        .build();
        final Request request = Request.builder()
                .metadata(metadata)
                .payload(PerformanceDataSpreadsheetProcessingRequestPayload.builder()
                        .accountReportFile(FileInfoDTO.builder().name("account1.xlsx").uuid(fileUUid).build())
                        .accountId(accountId)
                        .build())
                .build();
        final FileDTO accountReportFile = FileDTO.builder()
                .fileName("account1.xlsx")
                .build();
        final TP6PerformanceData performanceData = TP6PerformanceData.builder()
                .type(PerformanceDataTargetPeriodType.TP6)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .build();
        final TargetPeriodDocumentTemplate templateType = TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6;

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(fileAttachmentService.getFileDTO(fileUUid)).thenReturn(accountReportFile);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.getDocumentTemplateType())
                .thenReturn(templateType);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.getDocumentTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractData(metadata, accountReportFile))
                .thenReturn(performanceData);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.validateData(performanceData))
                .thenReturn(BusinessValidationResult.invalid(List.of(
                        new PerformanceDataUploadViolation("sectionName", PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA, "message1", "message2"),
                        new PerformanceDataUploadViolation("sectionName", PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA, "message3")
                )));

        // Invoke
        performanceDataSpreadsheetProcessingService.doProcess(requestId, accountReport);

        // Verify
        assertThat(((PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceData);
        assertThat(((PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload()).getPerformanceDataCalculatedMetrics())
                .isNull();
        assertThat(accountReport.getPerformanceDataCalculatedMetrics()).isNull();
        assertThat(accountReport.getErrors())
                .containsExactly("message1", "message2", "message3");
        verify(requestService, times(1)).findRequestById(requestId);
        verify(fileAttachmentService, times(1)).getFileDTO(fileUUid);
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .validateData(performanceData);
        verifyNoMoreInteractions(requestService, tp6PerformanceDataSpreadsheetProcessingExtractDataService, tp6PerformanceDataSpreadsheetTemplateValidator);
        verifyNoInteractions(accountReferenceDetailsService, underlyingAgreementQueryService, accountPerformanceDataStatusService);
    }

    @Test
    void doProcess_throw_exception() throws Exception {
        final String requestId = "requestId";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .errors(new ArrayList<>())
                .build();
        final String fileUUid = "fileUUid";
        final long accountId = 1L;
        final PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .build();
        final Request request = Request.builder()
                .metadata(metadata)
                .payload(PerformanceDataSpreadsheetProcessingRequestPayload.builder()
                        .accountReportFile(FileInfoDTO.builder().name("account1.xlsx").uuid(fileUUid).build())
                        .accountId(accountId)
                        .build())
                .build();
        final FileDTO accountReportFile = FileDTO.builder()
                .fileName("account1.xlsx")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(fileAttachmentService.getFileDTO(fileUUid)).thenReturn(accountReportFile);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.getDocumentTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetTemplateValidator.getDocumentTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetProcessingExtractDataService.extractData(metadata, accountReportFile))
                .thenThrow(new Exception("Test"));

        // Invoke
        BpmnExecutionException ex = assertThrows(BpmnExecutionException.class, () ->
                performanceDataSpreadsheetProcessingService.doProcess(requestId, accountReport));

        // Verify
        assertThat(ex.getErrors())
                .containsExactly(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.PROCESS_EXCEL_FAILED.getMessage());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(fileAttachmentService, times(1)).getFileDTO(fileUUid);
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetTemplateValidator, times(1))
                .getDocumentTemplateType();
        verify(tp6PerformanceDataSpreadsheetProcessingExtractDataService, times(1))
                .extractData(metadata, accountReportFile);
        verifyNoMoreInteractions(requestService);
        verifyNoInteractions(underlyingAgreementQueryService, accountReferenceDetailsService, accountPerformanceDataStatusService);
    }

    @Test
    void cleanupFailed() {
        final String file = "file";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .file(FileInfoDTO.builder().name("account1.xlsx").uuid(file).build())
                .build();

        // Invoke
        performanceDataSpreadsheetProcessingService.cleanupFailed(accountReport);

        // Verify
        verify(fileAttachmentService, times(1))
                .deleteFileAttachmentsInBatches(Set.of(file));
    }
}
