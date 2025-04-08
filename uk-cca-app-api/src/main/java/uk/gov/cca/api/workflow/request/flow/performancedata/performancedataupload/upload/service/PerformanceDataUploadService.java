package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.utils.ZipUtils;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUpload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation.PerformanceDataUploadAttachmentsExistValidatorService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation.PerformanceDataUploadExcelFileNameValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataUploadService {

    private final PerformanceDataUploadAttachmentsExistValidatorService performanceDataUploadAttachmentsExistValidatorService;
    private final FileAttachmentService fileAttachmentService;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private final PerformanceDataUploadExcelFileNameValidator performanceDataUploadExcelFileNameValidator;

    @Transactional
    public Map<Long, TargetUnitAccountUploadReport> submit(RequestTask requestTask, PerformanceDataUpload performanceDataUpload,
                                                           List<TargetUnitAccountBusinessInfoDTO> accounts) {
        final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        final PerformanceDataUploadRequestPayload requestPayload =
                (PerformanceDataUploadRequestPayload) requestTask.getRequest().getPayload();
        final Map<String, Long> accountsMap = accounts.stream()
                .collect(Collectors.toMap(TargetUnitAccountBusinessInfoDTO::getBusinessId, TargetUnitAccountBusinessInfoDTO::getAccountId));

        // Set request task payload with the final report package
        taskPayload.setPerformanceDataUpload(performanceDataUpload);

        // Validate files in payload
        BusinessValidationResult validationResult = performanceDataUploadAttachmentsExistValidatorService.validate(taskPayload);
        if(!validationResult.isValid()) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPLOAD, ValidatorHelper.extractViolations(List.of(validationResult)));
        }

        // Extract zips and persist individual account reports
        final Map<Long, TargetUnitAccountUploadReport> accountReportsFiles = new HashMap<>();
        int numOfFiles = 0;

        for (UUID reportPackageUuid : performanceDataUpload.getReportPackages()) {
            final FileDTO reportPackage = fileAttachmentService.getFileDTO(reportPackageUuid.toString());
            try {
                final Map<String, byte[]> reportFiles = ZipUtils.extractZipFiles(reportPackage.getFileContent());
                for (Map.Entry<String, byte[]> entry : reportFiles.entrySet()) {
                    // Increase number of files exist
                    numOfFiles++;

                    // Validate file name
                    BusinessValidationResult result = performanceDataUploadExcelFileNameValidator.validate(entry.getKey(), taskPayload.getSectorAssociationInfo(),
                            performanceDataUpload, accountsMap);

                    if(result.isValid()) {
                        // Add account to account report
                        addToAccountReportFile(entry, requestPayload.getSectorUserAssignee(), accountsMap, accountReportsFiles, taskPayload);
                    }
                    else {
                        // Add violations to errors
                        result.getViolations().forEach(violation ->
                                taskPayload.getErrors().put(entry.getKey(), ((PerformanceDataUploadViolation) violation).getMessage()));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new BusinessException(ErrorCode.INTERNAL_SERVER);
            }
        }

        // Set total files exist
        taskPayload.setTotalFilesUploaded(numOfFiles);

        // Return the report files for process
        return accountReportsFiles;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createCsvFile(RequestTask requestTask, Map<Long, TargetUnitAccountUploadReport> accountReports) {
        PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        try {
            // Write CSV
            if (!accountReports.isEmpty() || !taskPayload.getErrors().isEmpty()) {
                final PerformanceDataUploadRequestPayload requestPayload =
                        (PerformanceDataUploadRequestPayload) requestTask.getRequest().getPayload();
                final FileDTO csvFileDTO = PerformanceDataUploadUtility.createCsvFile(requestTask.getRequest().getId(),
                        accountReports.values().stream().toList(), taskPayload.getErrors());

                // Save to DB
                final String uuid = ccaFileAttachmentService.createSystemFileAttachment(
                        csvFileDTO, FileStatus.SUBMITTED, requestPayload.getSectorUserAssignee());

                FileInfoDTO csvFile = FileInfoDTO.builder().uuid(uuid).name(csvFileDTO.getFileName()).build();
                taskPayload.setCsvFile(csvFile);
            }
        } catch (Exception e) {
            log.error("Cannot generate csv for task {}", requestTask.getId(), e);
            throw new BpmnError("csvError");
        }
    }

    private void addToAccountReportFile(final Map.Entry<String, byte[]> entry, final String assignee, final Map<String, Long> accountsMap,
                                        Map<Long, TargetUnitAccountUploadReport> accountReportsFiles, PerformanceDataUploadSubmitRequestTaskPayload taskPayload) {
        final String accountBusinessId = PerformanceDataUploadUtility.extractBusinessAccountIdFromReportFilename(entry.getKey());
        Long accountId = accountsMap.get(accountBusinessId);

        final FileDTO excelFile = FileDTO.builder()
                .fileName(entry.getKey())
                .fileContent(entry.getValue())
                .fileSize(entry.getValue().length)
                .fileType(MimeTypeUtils.detect(entry.getValue(), entry.getKey())).build();

        try {
            if(!accountReportsFiles.containsKey(accountId)) {
                final String accountReportFileUuid = fileAttachmentService.createFileAttachment(
                        excelFile, FileStatus.SUBMITTED, assignee);

                // Add account to report map
                accountReportsFiles.put(
                        accountId,
                        TargetUnitAccountUploadReport.builder()
                                .accountId(accountId)
                                .accountBusinessId(accountBusinessId)
                                .file(FileInfoDTO.builder().name(entry.getKey()).uuid(accountReportFileUuid).build())
                                .build());
            }
            else {
                // Account is already contained in report map
                taskPayload.getErrors().put(
                        entry.getKey(),
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.MULTIPLE_FILES_FOR_ACCOUNT_FOUND.getMessage()
                );

                TargetUnitAccountUploadReport duplicateAccount = accountReportsFiles.get(accountId);
                taskPayload.getErrors().put(
                        duplicateAccount.getFile().getName(),
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.MULTIPLE_FILES_FOR_ACCOUNT_FOUND.getMessage()
                );
                accountReportsFiles.remove(accountId);
                fileAttachmentService.deleteFileAttachmentsInBatches(Set.of(duplicateAccount.getFile().getUuid()));
            }
        } catch (Exception e) {
            // If excel read fails add to task payload errors
            taskPayload.getErrors().put(
                    entry.getKey(),
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.READ_EXCEL_FAILED.getMessage()
            );
        }
    }
}
