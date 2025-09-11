package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.NotAccountRelatedUploadErrorReport;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataReportFileNameValidateServiceTest {

	@InjectMocks
	private PerformanceAccountTemplateDataReportFileNameValidateService cut;

	@Test
	void validateReportFilename_bad_filename() {
		String filename = "AIC-T0006501_PAT_TP6.xlsx";
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym("AIC").build();
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("AIC-T00001").build(),
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("AIC-T00002").build());

		FileReports fileReports = FileReports.builder().build();

		cut.validateReportFilename(filename, targetPeriodType, sectorAssociationInfo, eligibleAccounts, fileReports);

		assertThat(fileReports.getNotAccountRelatedFileErrors())
				.containsExactly(
						NotAccountRelatedUploadErrorReport.builder().fileName(filename).error(FilenameValidationErrorType.INVALID_FILE_NAME.getDescription()).build());
		assertThat(fileReports.getAccountFileReports()).isEmpty();
	}
	
	@Test
	void validateReportFilename_account_not_valid() {
		String filename = "AIC-T00001_PAT_TP6.xlsx";
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym("AIC").build();
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("AIC-T00002").build(),
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("AIC-T00003").build());

		FileReports fileReports = FileReports.builder().build();

		cut.validateReportFilename(filename, targetPeriodType, sectorAssociationInfo, eligibleAccounts, fileReports);

		assertThat(fileReports.getNotAccountRelatedFileErrors())
				.containsExactly(
						NotAccountRelatedUploadErrorReport.builder().fileName(filename).error(FilenameValidationErrorType.NOT_RELATED_TO_ELIGIBLE_ACCOUNT.getDescription()).build());
		assertThat(fileReports.getAccountFileReports()).isEmpty();
	}
	
	@Test
	void validateReportFilename_sector_not_match() {
		String filename = "AIC-T00001_PAT_TP6.xlsx";
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym("SEC").build();
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("AIC-T00001").build(),
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("AIC-T00003").build());

		FileReports fileReports = FileReports.builder().build();

		cut.validateReportFilename(filename, targetPeriodType, sectorAssociationInfo, eligibleAccounts, fileReports);

		assertThat(fileReports.getNotAccountRelatedFileErrors()).isEmpty();
		assertThat(fileReports.getAccountFileReports()).containsEntry(1L, AccountUploadReport.builder()
				.accountBusinessId("AIC-T00001")
				.accountId(1L)
				.succeeded(false)
				.errorFilenames(List.of(filename))
				.errors(List.of(
						FilenameValidationErrorType.SECTOR_IS_NOT_USERS_SECTOR.getDescription()
						))
				.build());
	}
	
	@Test
	void validateReportFilename_target_period_not_match() {
		String filename = "AIC-T00001_PAT_TP5.xlsx";
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym("AIC").build();
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("AIC-T00001").build(),
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("AIC-T00003").build());

		FileReports fileReports = FileReports.builder().build();

		cut.validateReportFilename(filename, targetPeriodType, sectorAssociationInfo, eligibleAccounts, fileReports);

		assertThat(fileReports.getNotAccountRelatedFileErrors()).isEmpty();
		assertThat(fileReports.getAccountFileReports()).containsEntry(1L, AccountUploadReport.builder()
				.accountBusinessId("AIC-T00001")
				.accountId(1L)
				.succeeded(false)
				.errorFilenames(List.of(filename))
				.errors(List.of(
						FilenameValidationErrorType.TARGET_PERIOD_NOT_MATCH_WITH_SELECTED_ONE.getDescription()
						))
				.build());
	}
	
	@Test
	void validateReportFilename_account_already_exist_in_reports() {
		String filename1 = "AIC-T00001_PAT_TP6.xlsx";
		String filename2 = "AIC-T00001_PAT_TP6.xlsx";
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym("AIC").build();
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("AIC-T00001").build(),
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("AIC-T00003").build());

		FileReports fileReports = FileReports.builder()
				.accountFileReports(new HashMap<>(Map.of(
						1L, AccountUploadReport.builder()
						.accountBusinessId("AIC-T00001")
						.accountId(1L)
						.file(FileInfoDTO.builder().name(filename1).build())
						.succeeded(true)
						.build()
						)))
				.build();

		cut.validateReportFilename(filename2, targetPeriodType, sectorAssociationInfo, eligibleAccounts, fileReports);

		assertThat(fileReports.getNotAccountRelatedFileErrors()).isEmpty();
		assertThat(fileReports.getAccountFileReports()).containsEntry(1L, AccountUploadReport.builder()
				.accountBusinessId("AIC-T00001")
				.accountId(1L)
				.succeeded(false)
				.file(null)
				.errorFilenames(List.of(filename1, filename2))
				.errors(List.of(
						FilenameValidationErrorType.MULTIPLE_FILES_PER_ACCOUNT.getDescription()
						))
				.build());
	}
	
	@Test
	void validateReportFilename() {
		String filename = "AIC-T00001_PAT_TP6.xlsx";
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym("AIC").build();
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("AIC-T00001").build(),
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("AIC-T00003").build());

		FileReports fileReports = FileReports.builder().build();

		cut.validateReportFilename(filename, targetPeriodType, sectorAssociationInfo, eligibleAccounts, fileReports);

		assertThat(fileReports.getNotAccountRelatedFileErrors()).isEmpty();
		assertThat(fileReports.getAccountFileReports()).containsEntry(1L, AccountUploadReport.builder()
				.accountBusinessId("AIC-T00001")
				.accountId(1L)
				.file(FileInfoDTO.builder().name(filename).build())
				.build());
	}
}
