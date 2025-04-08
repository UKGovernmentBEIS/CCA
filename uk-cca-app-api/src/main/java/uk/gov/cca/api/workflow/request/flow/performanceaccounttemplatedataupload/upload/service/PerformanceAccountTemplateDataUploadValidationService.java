package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.utils.ZipUtils;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.service.TargetPeriodEligibleAccountsQueryService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUpload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.ZipFileExtractor;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadValidationService {
	
	private final PerformanceAccountTemplateDataReportPackagesValidateExistenceService reportPackagesValidateExistenceService;
	private final TargetPeriodEligibleAccountsQueryService targetPeriodEligibleAccountsQueryService;
	private final FileAttachmentService fileAttachmentService;
	private final PerformanceAccountTemplateDataReportFileNameValidateService fileNameValidateService;
	
	public FileReports extractValidateAndPersistFiles(PerformanceAccountTemplateDataUpload dataUpload,
			Year targetPeriodYear, SectorAssociationInfo sector, String asigneeUser)
			throws ReportPackageMissingException {
		// validate existence of zip files
		reportPackagesValidateExistenceService.validateReportPackagesExistence(dataUpload.getReportPackages());
		
		// find eligible accounts
		final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = targetPeriodEligibleAccountsQueryService
				.getEligibleAccountsForPerformanceAccountTemplateReporting(sector.getId(),
						targetPeriodYear);
		
		// initiate file reports
		final FileReports fileReports = FileReports.builder().build();
		
		// validate individual files
		for (UUID reportPackageUuid : dataUpload.getReportPackages()) {
            for (String filename : extractReportPackageFilenames(reportPackageUuid)) {
				fileNameValidateService.validateReportFilename(filename, dataUpload.getTargetPeriodType(), sector,
						eligibleAccounts, fileReports);
            }
		}
		
		// persist files not failed validation 
		for (UUID reportPackageUuid : dataUpload.getReportPackages()) {
			final Map<String, byte[]> reportPackageFiles = extractReportPackageFiles(reportPackageUuid);
            for (Map.Entry<String, byte[]> entry : reportPackageFiles.entrySet()) {
				final String fileName = entry.getKey();
				
				final Optional<AccountUploadReport> reportOpt = fileReports.getAccountFileReports().values().stream()
						.filter(report -> BooleanUtils.isNotFalse(report.getSucceeded()) &&
									report.getFile() != null && 
									report.getFile().getName().equals(fileName))
						.findFirst();
				
				if(reportOpt.isEmpty()) {
					continue;
				}
				
				final AccountUploadReport report = reportOpt.get();
				
				final FileDTO file = FileDTO.builder()
		                .fileName(entry.getKey())
		                .fileContent(entry.getValue())
		                .fileSize(entry.getValue().length)
		                .fileType(MimeTypeUtils.detect(entry.getValue(), entry.getKey())).build();
				
				try {
					final String accountReportFileUuid = fileAttachmentService.createFileAttachment(
							file, FileStatus.SUBMITTED, asigneeUser);
					report.getFile().setUuid(accountReportFileUuid);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					
					report.setSucceeded(false);
					report.setFile(null);
					report.setErrorFilenames(List.of(fileName));
					report.setErrors(List.of(
							FilenameValidationErrorType.INTERNAL_SERVER_ERROR.getDescription()
							));
				}
            }
		}
		
		return fileReports;
	}

	private List<String> extractReportPackageFilenames(UUID reportPackageUuid) {
		final FileDTO reportPackage = fileAttachmentService.getFileDTO(reportPackageUuid.toString());
		try {
			return ZipFileExtractor.extractZipFilenames(reportPackage.getFileContent());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER);
		}
	}
	
	private Map<String, byte[]> extractReportPackageFiles(UUID reportPackageUuid) {
		final FileDTO reportPackage = fileAttachmentService.getFileDTO(reportPackageUuid.toString());
		return ZipUtils.extractZipFiles(reportPackage.getFileContent());
	}
}
