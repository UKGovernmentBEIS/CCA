package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.utils.ZipUtils;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUpload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation.PerformanceDataUploadAttachmentsExistValidatorService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation.PerformanceDataUploadExcelFileNameValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadServiceTest {

    @InjectMocks
    private PerformanceDataUploadService performanceDataUploadService;

    @Mock
    private PerformanceDataUploadAttachmentsExistValidatorService performanceDataUploadAttachmentsExistValidatorService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Mock
    private PerformanceDataUploadExcelFileNameValidator performanceDataUploadExcelFileNameValidator;

    @Test
    void submit() throws IOException {
        final UUID zip = UUID.randomUUID();
        final UUID excel = UUID.randomUUID();
        final String fileName = "ADS_1-T00001_TPR_TP6_V1.xlsx";
        final FileDTO excelFile = FileDTO.builder()
                .fileName(fileName)
                .fileType("text/plain")
                .fileContent("Test".getBytes())
                .fileSize(4)
                .build();
        final byte[] zipContent = ZipUtils.generateZipFile(List.of(excelFile));
        final FileDTO zipFile = FileDTO.builder().fileContent(zipContent).build();
        final String sectorUserAssignee = "sectorUserAssignee";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .totalFilesUploaded(0)
                        .build())
                .request(Request.builder()
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .reportPackages(Set.of(zip))
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("ADS_1-T00001").build()
        );
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        when(performanceDataUploadAttachmentsExistValidatorService
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentService.getFileDTO(zip.toString())).thenReturn(zipFile);
        when(fileAttachmentService.createFileAttachment(excelFile, FileStatus.SUBMITTED, sectorUserAssignee))
                .thenReturn(excel.toString());
        when(performanceDataUploadExcelFileNameValidator.validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        Map<Long, TargetUnitAccountUploadReport> result = performanceDataUploadService
                .submit(requestTask, performanceDataUpload, accounts);

        // Verify
        assertThat(result).containsExactlyEntriesOf(Map.of(1L,
                TargetUnitAccountUploadReport.builder()
                        .accountId(1L)
                        .accountBusinessId("ADS_1-T00001")
                        .file(FileInfoDTO.builder().name(excelFile.getFileName()).uuid(excel.toString()).build())
                        .build()
        ));
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getPerformanceDataUpload())
                .isEqualTo(performanceDataUpload);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getErrors())
                .isEmpty();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getTotalFilesUploaded())
                .isEqualTo(1);
        verify(performanceDataUploadAttachmentsExistValidatorService, times(1))
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload());
        verify(fileAttachmentService, times(1)).getFileDTO(zip.toString());
        verify(fileAttachmentService, times(1))
                .createFileAttachment(excelFile, FileStatus.SUBMITTED, sectorUserAssignee);
        verify(performanceDataUploadExcelFileNameValidator, times(1))
                .validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap);
        verifyNoMoreInteractions(fileAttachmentService);
    }

    @Test
    void submit_filename_not_valid() throws IOException {
        final UUID zip = UUID.randomUUID();
        final String fileName = "ADS_1-T00001_TPR_TP70_V1.xlsx";
        final FileDTO excelFile = FileDTO.builder()
                .fileName(fileName)
                .fileType("text/plain")
                .fileContent("Test".getBytes())
                .fileSize(4)
                .build();
        final byte[] zipContent = ZipUtils.generateZipFile(List.of(excelFile));
        final FileDTO zipFile = FileDTO.builder().fileContent(zipContent).build();
        final String sectorUserAssignee = "sectorUserAssignee";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .totalFilesUploaded(0)
                        .build())
                .request(Request.builder()
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .reportPackages(Set.of(zip))
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("ADS_1-T00001").build()
        );
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        when(performanceDataUploadAttachmentsExistValidatorService
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentService.getFileDTO(zip.toString())).thenReturn(zipFile);
        when(performanceDataUploadExcelFileNameValidator.validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap))
                .thenReturn(BusinessValidationResult.invalid(List.of(
                        new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.FILE_NAME_NOT_VALID)
                )));

        // Invoke
        Map<Long, TargetUnitAccountUploadReport> result = performanceDataUploadService
                .submit(requestTask, performanceDataUpload, accounts);

        // Verify
        assertThat(result).isEmpty();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getPerformanceDataUpload())
                .isEqualTo(performanceDataUpload);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getErrors())
                .containsExactlyEntriesOf(Map.of(
                        fileName,
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.FILE_NAME_NOT_VALID.getMessage()
                ));
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getTotalFilesUploaded())
                .isEqualTo(1);
        verify(performanceDataUploadAttachmentsExistValidatorService, times(1))
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload());
        verify(fileAttachmentService, times(1)).getFileDTO(zip.toString());
        verify(performanceDataUploadExcelFileNameValidator, times(1))
                .validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap);
        verify(fileAttachmentService, never()).createFileAttachment(any(), any(), anyString());
        verifyNoMoreInteractions(fileAttachmentService);
    }

    @Test
    void submit_throw_exception() throws IOException {
        final UUID zip = UUID.randomUUID();
        final String fileName = "ADS_1-T00001_TPR_TP6_V1.xlsx";
        final FileDTO excelFile = FileDTO.builder()
                .fileName(fileName)
                .fileType("text/plain")
                .fileContent("Test".getBytes())
                .fileSize(4)
                .build();
        final byte[] zipContent = ZipUtils.generateZipFile(List.of(excelFile));
        final FileDTO zipFile = FileDTO.builder().fileContent(zipContent).build();
        final String sectorUserAssignee = "sectorUserAssignee";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .totalFilesUploaded(0)
                        .build())
                .request(Request.builder()
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .reportPackages(Set.of(zip))
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("ADS_1-T00001").build()
        );
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        when(performanceDataUploadAttachmentsExistValidatorService
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentService.getFileDTO(zip.toString())).thenReturn(zipFile);
        when(fileAttachmentService.createFileAttachment(excelFile, FileStatus.SUBMITTED, sectorUserAssignee))
                .thenThrow(new IOException("test"));
        when(performanceDataUploadExcelFileNameValidator.validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        Map<Long, TargetUnitAccountUploadReport> result = performanceDataUploadService
                .submit(requestTask, performanceDataUpload, accounts);

        // Verify
        assertThat(result).isEmpty();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getErrors())
                .containsExactlyEntriesOf(Map.of(
                        fileName,
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.READ_EXCEL_FAILED.getMessage()
                ));
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getTotalFilesUploaded())
                .isEqualTo(1);
        verify(performanceDataUploadAttachmentsExistValidatorService, times(1))
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload());
        verify(fileAttachmentService, times(1)).getFileDTO(zip.toString());
        verify(fileAttachmentService, times(1))
                .createFileAttachment(excelFile, FileStatus.SUBMITTED, sectorUserAssignee);
        verify(performanceDataUploadExcelFileNameValidator, times(1))
                .validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap);
        verifyNoMoreInteractions(fileAttachmentService);
    }

    @Test
    void submit_duplicate_account() throws IOException {
        final UUID zip = UUID.randomUUID();
        final UUID excel2 = UUID.randomUUID();
        final String fileName = "ADS_1-T00001_TPR_TP6_V1.xlsx";
        final FileDTO excelFile = FileDTO.builder()
                .fileName(fileName)
                .fileType("text/plain")
                .fileContent("Test".getBytes())
                .fileSize(4)
                .build();
        final String fileName2 = "ADS_1-T00001_TPR_TP6_V2.xlsx";
        final FileDTO excelFile2 = FileDTO.builder()
                .fileName(fileName2)
                .fileType("text/plain")
                .fileContent("Test".getBytes())
                .fileSize(4)
                .build();
        final byte[] zipContent = ZipUtils.generateZipFile(List.of(excelFile, excelFile2));
        final FileDTO zipFile = FileDTO.builder().fileContent(zipContent).build();
        final String sectorUserAssignee = "sectorUserAssignee";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .totalFilesUploaded(0)
                        .build())
                .request(Request.builder()
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .reportPackages(Set.of(zip))
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("ADS_1-T00001").build()
        );
        final Map<String, Long> accountsMap = Map.of("ADS_1-T00001", 1L);

        when(performanceDataUploadAttachmentsExistValidatorService
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentService.getFileDTO(zip.toString())).thenReturn(zipFile);
        when(fileAttachmentService.createFileAttachment(excelFile2, FileStatus.SUBMITTED, sectorUserAssignee))
                .thenReturn(excel2.toString());
        when(performanceDataUploadExcelFileNameValidator.validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataUploadExcelFileNameValidator.validate(fileName2, sectorAssociationInfo, performanceDataUpload, accountsMap))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        Map<Long, TargetUnitAccountUploadReport> result = performanceDataUploadService
                .submit(requestTask, performanceDataUpload, accounts);

        // Verify
        assertThat(result).isEmpty();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getPerformanceDataUpload())
                .isEqualTo(performanceDataUpload);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getErrors())
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        fileName2,
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.MULTIPLE_FILES_FOR_ACCOUNT_FOUND.getMessage(),
                        fileName,
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.MULTIPLE_FILES_FOR_ACCOUNT_FOUND.getMessage()
                ));
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getTotalFilesUploaded())
                .isEqualTo(2);
        verify(performanceDataUploadAttachmentsExistValidatorService, times(1))
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload());
        verify(fileAttachmentService, times(1)).getFileDTO(zip.toString());
        verify(fileAttachmentService, times(1))
                .createFileAttachment(excelFile2, FileStatus.SUBMITTED, sectorUserAssignee);
        verify(performanceDataUploadExcelFileNameValidator, times(1))
                .validate(fileName, sectorAssociationInfo, performanceDataUpload, accountsMap);
        verify(performanceDataUploadExcelFileNameValidator, times(1))
                .validate(fileName2, sectorAssociationInfo, performanceDataUpload, accountsMap);
        verify(fileAttachmentService, times(1))
                .deleteFileAttachmentsInBatches(Set.of(excel2.toString()));
    }

    @Test
    void submit_not_valid_files() {
        final UUID zip = UUID.randomUUID();
        final String sectorUserAssignee = "sectorUserAssignee";
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .totalFilesUploaded(0)
                        .build())
                .request(Request.builder()
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        final PerformanceDataUpload performanceDataUpload = PerformanceDataUpload.builder()
                .performanceDataTargetPeriodType(PerformanceDataTargetPeriodType.TP6)
                .reportPackages(Set.of(zip))
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("ADS_1-T00001").build()
        );

        when(performanceDataUploadAttachmentsExistValidatorService
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()))
                .thenReturn(BusinessValidationResult.invalid(List.of(new PerformanceDataUploadViolation(
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.ATTACHMENT_NOT_FOUND
                ))));

        // Invoke
        BusinessException ex = Assertions.assertThrows(BusinessException.class, () ->
                performanceDataUploadService.submit(requestTask, performanceDataUpload, accounts));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPLOAD);
        verify(performanceDataUploadAttachmentsExistValidatorService, times(1))
                .validate((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload());
        verifyNoInteractions(fileAttachmentService, performanceDataUploadExcelFileNameValidator);
    }

    @Test
    void createCsvFile() throws IOException {
        final String sectorUserAssignee = "sectorUserAssignee";
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .errors(Map.of("error", "error"))
                        .build())
                .request(Request.builder()
                        .id("requestId")
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        Map<Long, TargetUnitAccountUploadReport> accountReports = Map.of(
                1L, TargetUnitAccountUploadReport.builder()
                        .succeeded(true)
                        .accountBusinessId("account1")
                        .file(FileInfoDTO.builder().name("account1.xlsx").build())
                        .build(),
                2L, TargetUnitAccountUploadReport.builder()
                        .succeeded(false)
                        .accountBusinessId("account2")
                        .file(FileInfoDTO.builder().name("account2.xlsx").build())
                        .errors(List.of("account error"))
                        .build()
        );
        final String fileCsv = "fileCsv";

        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee)))
                .thenReturn(fileCsv);

        // Invoke
        performanceDataUploadService.createCsvFile(requestTask, accountReports);

        // Verify
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getCsvFile())
                .isNotNull();
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee));
    }

    @Test
    void createCsvFile_throw_exception() throws IOException {
        final String sectorUserAssignee = "sectorUserAssignee";
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .errors(Map.of("error", "error"))
                        .build())
                .request(Request.builder()
                        .id("requestId")
                        .payload(PerformanceDataUploadRequestPayload.builder()
                                .sectorUserAssignee(sectorUserAssignee)
                                .build())
                        .build())
                .build();
        Map<Long, TargetUnitAccountUploadReport> accountReports = Map.of(
                1L, TargetUnitAccountUploadReport.builder()
                        .succeeded(true)
                        .accountBusinessId("account1")
                        .file(FileInfoDTO.builder().name("account1.xlsx").build())
                        .build(),
                2L, TargetUnitAccountUploadReport.builder()
                        .succeeded(false)
                        .accountBusinessId("account2")
                        .file(FileInfoDTO.builder().name("account2.xlsx").build())
                        .errors(List.of("account error"))
                        .build()
        );

        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee)))
                .thenThrow(new IOException("test"));

        // Invoke
        BpmnError ex = assertThrows(BpmnError.class, () ->
                performanceDataUploadService.createCsvFile(requestTask, accountReports));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo("csvError");
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee));
    }
}
