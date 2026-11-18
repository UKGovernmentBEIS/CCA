package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.utils.ZipUtils;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.service.TargetPeriodEligibleAccountsQueryService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUpload;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadValidationServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadValidationService cut;
	
	@Mock
	private PerformanceAccountTemplateDataReportPackagesValidateExistenceService reportPackagesValidateExistenceService;
	
	@Mock
	private TargetPeriodEligibleAccountsQueryService targetPeriodEligibleAccountsQueryService;
	
	@Mock
	private FileAttachmentService fileAttachmentService;
	
	@Mock
	private PerformanceAccountTemplateDataReportFileNameValidateService fileNameValidateService;

	@Test
	void extractValidateAndPersistFiles() throws ReportPackageMissingException, IOException {
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		Year targetPeriodYear = Year.of(2024);
		UUID reportPackage1 = UUID.randomUUID();
		UUID reportPackage2 = UUID.randomUUID();
		PerformanceAccountTemplateDataUpload dataUpload = PerformanceAccountTemplateDataUpload.builder()
				.targetPeriodType(targetPeriodType)
				.reportPackages(Set.of(
						reportPackage1,
						reportPackage2
						))
				.build();
		
		SectorAssociationInfo sector = SectorAssociationInfo.builder()
				.id(1L)
				.build();
		
		String asigneeUser = "Assignee";
		
		List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).build()
				);
		
		when(targetPeriodEligibleAccountsQueryService
				.getEligibleAccountsForPerformanceAccountTemplateReporting(sector.getId(), targetPeriodYear))
				.thenReturn(eligibleAccounts);
		
		FileDTO file1 = FileDTO.builder().fileName("zip1file1").fileContent("Test1".getBytes()).build();
		FileDTO file2 = FileDTO.builder().fileName("zip1file2").fileContent("Test2".getBytes()).build();
		final FileDTO reportPackageFile1 = FileDTO.builder()
				.fileContent(ZipUtils.generateZipFile(List.of(
						file1,
						file2
						)))
				.build();
		
		FileDTO file3 = FileDTO.builder().fileName("zip2file1").fileContent("Test3".getBytes()).build();
		FileDTO file4 = FileDTO.builder().fileName("zip2file2").fileContent("Test4".getBytes()).build();
		final FileDTO reportPackageFile2 = FileDTO.builder()
				.fileContent(ZipUtils.generateZipFile(List.of(
						file3,
						file4
						)))
				.build();
		
		when(fileAttachmentService.getFileDTO(reportPackage1.toString())).thenReturn(reportPackageFile1);
		when(fileAttachmentService.getFileDTO(reportPackage2.toString())).thenReturn(reportPackageFile2);
		
		
		doAnswer(invocation -> {
            FileReports reports = invocation.getArgument(4);
            reports.getAccountFileReports().put(1L, AccountUploadReport.builder()
            		.succeeded(true)
            		.accountId(1L)
            		.file(FileInfoDTO.builder().name(file1.getFileName()).build())
            		.build());
            return null;
        }).when(fileNameValidateService).validateReportFilename(Mockito.eq("zip1file1"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any(FileReports.class));
		
		doAnswer(invocation -> {
			FileReports reports = invocation.getArgument(4);
            reports.getAccountFileReports().put(2L, AccountUploadReport.builder()
            		.succeeded(false)
            		.accountId(2L)
            		.build());
            return null;
        }).when(fileNameValidateService).validateReportFilename(Mockito.eq("zip1file2"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any(FileReports.class));

		
		doAnswer(invocation -> {
            FileReports reports = invocation.getArgument(4);
            reports.getAccountFileReports().put(3L, AccountUploadReport.builder()
            		.succeeded(true)
            		.accountId(3L)
            		.file(FileInfoDTO.builder().name(file3.getFileName()).build())
            		.build());
            return null;
        }).when(fileNameValidateService).validateReportFilename(Mockito.eq("zip2file1"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any(FileReports.class));
		
		doAnswer(invocation -> {
			return null;
        }).when(fileNameValidateService).validateReportFilename(Mockito.eq("zip2file2"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any(FileReports.class));

		UUID zip1File1Uuid = UUID.randomUUID();
		when(fileAttachmentService.createFileAttachment(FileDTO.builder()
		                .fileName(file1.getFileName())
		                .fileContent(file1.getFileContent())
		                .fileSize(file1.getFileContent().length)
		                .fileType(MimeTypeUtils.detect(file1.getFileContent(), file1.getFileName())).build(), FileStatus.SUBMITTED, asigneeUser)).thenReturn(zip1File1Uuid.toString());
		
		UUID zip2File1Uuid = UUID.randomUUID();
		when(fileAttachmentService.createFileAttachment(FileDTO.builder()
		                .fileName(file3.getFileName())
		                .fileContent(file3.getFileContent())
		                .fileSize(file3.getFileContent().length)
		                .fileType(MimeTypeUtils.detect(file3.getFileContent(), file3.getFileName())).build(), FileStatus.SUBMITTED, asigneeUser)).thenReturn(zip2File1Uuid.toString());
		
		
		//invoke
		var result = cut.extractValidateAndPersistFiles(dataUpload, targetPeriodYear, sector, asigneeUser);
		
		
		verify(reportPackagesValidateExistenceService, times(1))
				.validateReportPackagesExistence(dataUpload.getReportPackages());
		verify(targetPeriodEligibleAccountsQueryService, times(1))
				.getEligibleAccountsForPerformanceAccountTemplateReporting(sector.getId(), targetPeriodYear);
		verify(fileNameValidateService, times(1))
			.validateReportFilename(Mockito.eq("zip1file1"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any());
		verify(fileNameValidateService, times(1))
			.validateReportFilename(Mockito.eq("zip1file2"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any());
		verify(fileNameValidateService, times(1))
			.validateReportFilename(Mockito.eq("zip2file1"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any());
		verify(fileNameValidateService, times(1))
			.validateReportFilename(Mockito.eq("zip2file2"), Mockito.eq(targetPeriodType), Mockito.eq(sector), Mockito.eq(eligibleAccounts), Mockito.any());
		
		verify(fileAttachmentService, times(1)).createFileAttachment(FileDTO.builder()
	                .fileName(file1.getFileName())
	                .fileContent(file1.getFileContent())
	                .fileSize(file1.getFileContent().length)
	                .fileType(MimeTypeUtils.detect(file1.getFileContent(), file1.getFileName())).build(),
                FileStatus.SUBMITTED, asigneeUser);
		
		verify(fileAttachmentService, times(1)).createFileAttachment(FileDTO.builder()
                .fileName(file3.getFileName())
                .fileContent(file3.getFileContent())
                .fileSize(file3.getFileContent().length)
                .fileType(MimeTypeUtils.detect(file3.getFileContent(), file3.getFileName())).build(),
            FileStatus.SUBMITTED, asigneeUser);
		
		assertThat(result.getAccountFileReports().get(1L).getFile().getUuid()).isEqualTo(zip1File1Uuid.toString());
		assertThat(result.getAccountFileReports().get(3L).getFile().getUuid()).isEqualTo(zip2File1Uuid.toString());
	}
}
