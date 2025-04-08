package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.NotAccountRelatedUploadErrorReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.utils.PerformanceAccountTemplateUploadUtils;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Service
class PerformanceAccountTemplateDataReportFileNameValidateService {

	public void validateReportFilename(String filename, TargetPeriodType targetPeriodType,
			SectorAssociationInfo sectorAssociationInfo, List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts, 
			FileReports fileReportsResult) {
		Matcher matcher = Pattern.compile(PerformanceAccountTemplateUploadUtils.REPORT_FILE_NAME_REGEX).matcher(filename);
		
		// check if file name is valid 
		if (!matcher.matches()) {
			fileReportsResult.getNotAccountRelatedFileErrors()
					.add(NotAccountRelatedUploadErrorReport.builder().fileName(filename)
							.error(FilenameValidationErrorType.INVALID_FILE_NAME.getDescription()).build());
			return;
		}
		
		// check if the target unit account is valid (eligible to upload PAT)
		final String targetUnitAccountMatched = matcher.group("targetunit");
		final Optional<Long> accountIdOpt = eligibleAccounts.stream()
                .filter(acc -> targetUnitAccountMatched.equals(acc.getBusinessId()))
                .map(TargetUnitAccountBusinessInfoDTO::getAccountId)
                .findFirst();
		
		if(accountIdOpt.isEmpty()) {
			fileReportsResult.getNotAccountRelatedFileErrors()
					.add(NotAccountRelatedUploadErrorReport.builder().fileName(filename)
							.error(FilenameValidationErrorType.NOT_RELATED_TO_ELIGIBLE_ACCOUNT.getDescription()).build());
			return;
		}
		
		final Long accountId = accountIdOpt.get();
		
		// check if the sector is the same as the current user's sector
		final String sectorMatched = matcher.group("sector");
		if(!sectorAssociationInfo.getAcronym().equals(sectorMatched)) {
			fileReportsResult.getAccountFileReports().put(accountId, AccountUploadReport.builder()
					.accountBusinessId(targetUnitAccountMatched)
					.accountId(accountId)
					.succeeded(false)
					.errorFilenames(List.of(filename))
					.errors(List.of(
							FilenameValidationErrorType.SECTOR_IS_NOT_USERS_SECTOR.getDescription()
							))
					.build());
			return;
		}
		
		// check if the target period matches with the selected one
		final String targetPeriodMatched = matcher.group("targetperiod");
		if(!targetPeriodType.name().equals(targetPeriodMatched)) {
			fileReportsResult.getAccountFileReports().put(accountId, AccountUploadReport.builder()
					.accountBusinessId(targetUnitAccountMatched)
					.accountId(accountId)
					.succeeded(false)
					.errorFilenames(List.of(filename))
					.errors(List.of(
							FilenameValidationErrorType.TARGET_PERIOD_NOT_MATCH_WITH_SELECTED_ONE.getDescription()
							))
					.build());
			return;
		}
		
		//check if account already exists in the report
		if(fileReportsResult.getAccountFileReports().containsKey(accountId)) {
			final AccountUploadReport accountReport = fileReportsResult.getAccountFileReports().get(accountId);
			if(accountReport.getFile() != null) {
				String previousFileName = accountReport.getFile().getName();
				accountReport.getErrorFilenames().add(previousFileName);
			}
			accountReport.setSucceeded(false);
			accountReport.setFile(null);
			accountReport.getErrorFilenames().add(filename);
			accountReport.setErrors(List.of(
					FilenameValidationErrorType.MULTIPLE_FILES_PER_ACCOUNT.getDescription()
					));
			return;
		}
		
		// validation passed
		fileReportsResult.getAccountFileReports().put(accountIdOpt.get(), AccountUploadReport.builder()
				.accountBusinessId(targetUnitAccountMatched)
				.accountId(accountIdOpt.get())
				.file(FileInfoDTO.builder().name(filename).build())
				.build());
		
	}
}
