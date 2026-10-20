package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.service.TP6PerformanceDataSpreadsheetGenerateExcelService;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetDoGenerateServiceTest {

    @InjectMocks
    private PerformanceDataSpreadsheetDoGenerateService performanceDataSpreadsheetDoGenerateService;

    @Mock
    private RequestService requestService;

    @Spy
    private ArrayList<PerformanceDataSpreadsheetGenerateExcelService> performanceDataSpreadsheetGenerateExcelServices;

    @Mock
    private TP6PerformanceDataSpreadsheetGenerateExcelService tp6PerformanceDataSpreadsheetGenerateExcelService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @BeforeEach
    void setUp() {
        performanceDataSpreadsheetGenerateExcelServices.add(
                tp6PerformanceDataSpreadsheetGenerateExcelService);
    }

    @Test
    void doGenerate() throws Exception {
        final String requestId = "requestId";
        final long accountId = 1L;

        final String sectorUserAssignee = "sector";
        final String fileName = "excel.xlsx";
        final String fileUUID = UUID.randomUUID().toString();
        final PerformanceDataSpreadsheetGenerateRequestMetadata metadata = PerformanceDataSpreadsheetGenerateRequestMetadata.builder()
                .targetPeriodDocument(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(metadata)
                .payload(PerformanceDataSpreadsheetGenerateRequestPayload.builder()
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .build();
        final FileDTO report = FileDTO.builder().fileName(fileName).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(tp6PerformanceDataSpreadsheetGenerateExcelService.getTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetGenerateExcelService.generate(metadata, accountId)).thenReturn(report);
        when(fileAttachmentService.createFileAttachment(report, FileStatus.SUBMITTED, sectorUserAssignee)).thenReturn(fileUUID);

        // Invoke
        FileInfoDTO result = performanceDataSpreadsheetDoGenerateService.doGenerate(requestId, accountId);

        // Verify
        assertThat(result)
                .isNotNull()
                .isEqualTo(FileInfoDTO.builder().name(fileName).uuid(fileUUID).build());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(tp6PerformanceDataSpreadsheetGenerateExcelService, times(1))
                .getTemplateType();
        verify(tp6PerformanceDataSpreadsheetGenerateExcelService, times(1))
                .generate(metadata, accountId);
        verify(fileAttachmentService, times(1))
                .createFileAttachment(report, FileStatus.SUBMITTED, sectorUserAssignee);
    }

    @Test
    void doGenerate_error() throws Exception {
        final String requestId = "requestId";
        final long accountId = 1L;

        final String sectorUserAssignee = "sector";
        final String fileName = "excel.xlsx";
        final PerformanceDataSpreadsheetGenerateRequestMetadata metadata = PerformanceDataSpreadsheetGenerateRequestMetadata.builder()
                .targetPeriodDocument(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(metadata)
                .payload(PerformanceDataSpreadsheetGenerateRequestPayload.builder()
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .build();
        final FileDTO report = FileDTO.builder().fileName(fileName).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(tp6PerformanceDataSpreadsheetGenerateExcelService.getTemplateType())
                .thenReturn(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6);
        when(tp6PerformanceDataSpreadsheetGenerateExcelService.generate(metadata, accountId)).thenReturn(report);
        when(fileAttachmentService.createFileAttachment(report, FileStatus.SUBMITTED, sectorUserAssignee))
                .thenThrow(new IOException("Exception"));

        // Invoke
        BpmnExecutionException ex = assertThrows(BpmnExecutionException.class, () ->
                performanceDataSpreadsheetDoGenerateService.doGenerate(requestId, accountId));

        // Verify
        assertThat(ex.getErrors()).containsExactly("Generate excel failed");
        verify(requestService, times(1)).findRequestById(requestId);
        verify(tp6PerformanceDataSpreadsheetGenerateExcelService, times(1))
                .getTemplateType();
        verify(tp6PerformanceDataSpreadsheetGenerateExcelService, times(1))
                .generate(metadata, accountId);
        verify(fileAttachmentService, times(1))
                .createFileAttachment(report, FileStatus.SUBMITTED, sectorUserAssignee);
    }
}
