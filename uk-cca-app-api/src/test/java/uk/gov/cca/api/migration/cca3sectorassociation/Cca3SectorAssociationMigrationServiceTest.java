package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationMigrationServiceTest {

	@InjectMocks
	private Cca3SectorAssociationMigrationService migrationService;

	@Mock
	private FtpProperties ftpProperties;

	@Mock
	private FtpFileService ftpFileService;

	@Mock
	private Cca3SectorAssociationMigrationValidationService validationService;

	@Mock
	private Cca3SectorAssociationMigrationUpdateService updateService;

	@Test
	void migrate() {
		final String directory = "directory";
		final String sourceFile = "migration.csv";
		final String filePath = directory + "/" + sourceFile;
		final String content = "sector_acronym,subsector_name,energy_or_carbon_unit,tp7_improvement,tp8_improvement,tp9_improvement,uma_date,sector_definition\n"
				+ "ADS_11,SUBSECTOR_1,Energy (kWh),2.000%,3.000%,4.000%,01/01/2026,sector definition";

		final FtpFileDTOResult fileDTOResult = FtpFileDTOResult.builder()
				.fileDTO(FileDTO.builder()
						.fileName(sourceFile)
						.fileContent(content.getBytes())
						.build())
				.build();
		final List<Cca3SectorAssociationVO> dataList = List.of(
				Cca3SectorAssociationVO.builder()
						.sectorAcronym("ADS_11")
						.subsectorName("SUBSECTOR_1")
						.measurementType(MeasurementType.ENERGY_KWH)
						.targetPeriod7Improvement(BigDecimal.valueOf(2).setScale(7, RoundingMode.HALF_DOWN))
						.targetPeriod8Improvement(BigDecimal.valueOf(3).setScale(7, RoundingMode.HALF_DOWN))
						.targetPeriod9Improvement(BigDecimal.valueOf(4).setScale(7, RoundingMode.HALF_DOWN))
						.umaDate(LocalDate.of(2026, 1, 1))
						.sectorDefinition("sector definition")
						.build()
		);

		when(ftpProperties.getServerCca3SectorAssociationMigrationDirectory()).thenReturn(directory);
		when(ftpProperties.getServerCca3SectorAssociationMigrationSourceFile()).thenReturn(sourceFile);
		when(ftpFileService.fetchFile(filePath)).thenReturn(fileDTOResult);

		// Invoke
		List<String> result = migrationService.migrate("");

		// Verify
		assertThat(result).isEmpty();
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationDirectory();
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationSourceFile();
		verify(ftpFileService, times(1)).fetchFile(filePath);
		verify(validationService, times(1)).validate(dataList, new ArrayList<>());
		verify(updateService, times(1)).updateSectorAssociationDataList(dataList);
	}

	@Test
	void migrate_with_errors() {
		final String directory = "directory";
		final String sourceFile = "migration.csv";
		final String filePath = directory + "/" + sourceFile;
		final String content = "sector_acronym,subsector_name,energy_or_carbon_unit,tp7_improvement,tp8_improvement,tp9_improvement,uma_date,sector_definition\n"
				+ ",SUBSECTOR_1,Energy (kWh),2.000%,3.000%,4.000%,01/01/2026,sector definition";

		final FtpFileDTOResult fileDTOResult = FtpFileDTOResult.builder()
				.fileDTO(FileDTO.builder()
						.fileName(sourceFile)
						.fileContent(content.getBytes())
						.build())
				.build();

		when(ftpProperties.getServerCca3SectorAssociationMigrationDirectory()).thenReturn(directory);
		when(ftpProperties.getServerCca3SectorAssociationMigrationSourceFile()).thenReturn(sourceFile);
		when(ftpFileService.fetchFile(filePath)).thenReturn(fileDTOResult);

		// Invoke
		List<String> result = migrationService.migrate("");

		// Verify
		assertThat(result).containsExactly("[2] Field 'sectorAcronym' is mandatory but no value was provided.");
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationDirectory();
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationSourceFile();
		verify(ftpFileService, times(1)).fetchFile(filePath);
		verify(validationService, times(1)).validate(anyList(), anyList());
		verifyNoInteractions(updateService);
	}

	@Test
	void migrate_throw_error() {
		final String directory = "directory";
		final String sourceFile = "migration.csv";
		final String filePath = directory + "/" + sourceFile;
		final String content = "sector_acronym,subsector_name,energy_or_carbon_unit,tp7_improvement,tp8_improvement,tp9_improvement,uma_date,sector_definition\n"
				+ "ADS_11,SUBSECTOR_1,Energy (kWh),2.000%,3.000%,4.000%,01/01/2026,sector definition";

		final FtpFileDTOResult fileDTOResult = FtpFileDTOResult.builder()
				.fileDTO(FileDTO.builder()
						.fileName(sourceFile)
						.fileContent(content.getBytes())
						.build())
				.build();
		final List<Cca3SectorAssociationVO> dataList = List.of(
				Cca3SectorAssociationVO.builder()
						.sectorAcronym("ADS_11")
						.subsectorName("SUBSECTOR_1")
						.measurementType(MeasurementType.ENERGY_KWH)
						.targetPeriod7Improvement(BigDecimal.valueOf(2).setScale(7, RoundingMode.HALF_DOWN))
						.targetPeriod8Improvement(BigDecimal.valueOf(3).setScale(7, RoundingMode.HALF_DOWN))
						.targetPeriod9Improvement(BigDecimal.valueOf(4).setScale(7, RoundingMode.HALF_DOWN))
						.umaDate(LocalDate.of(2026, 1, 1))
						.sectorDefinition("sector definition")
						.build()
		);

		when(ftpProperties.getServerCca3SectorAssociationMigrationDirectory()).thenReturn(directory);
		when(ftpProperties.getServerCca3SectorAssociationMigrationSourceFile()).thenReturn(sourceFile);
		when(ftpFileService.fetchFile(filePath)).thenReturn(fileDTOResult);
		doThrow(new RuntimeException("Error exception")).when(updateService).updateSectorAssociationDataList(dataList);

		// Invoke
		List<String> result = migrationService.migrate("");

		// Verify
		assertThat(result).containsExactly("Error exception");
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationDirectory();
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationSourceFile();
		verify(ftpFileService, times(1)).fetchFile(filePath);
		verify(validationService, times(1)).validate(eq(dataList), anyList());
		verify(updateService, times(1)).updateSectorAssociationDataList(dataList);
	}

	@Test
	void migrate_with_file_report_error() {
		final String directory = "directory";
		final String sourceFile = "migration.csv";
		final String filePath = directory + "/" + sourceFile;

		final FtpFileDTOResult fileDTOResult = FtpFileDTOResult.builder()
				.errorReport("Error report")
				.build();

		when(ftpProperties.getServerCca3SectorAssociationMigrationDirectory()).thenReturn(directory);
		when(ftpProperties.getServerCca3SectorAssociationMigrationSourceFile()).thenReturn(sourceFile);
		when(ftpFileService.fetchFile(filePath)).thenReturn(fileDTOResult);

		// Invoke
		FtpFileGenericException ex = assertThrows(FtpFileGenericException.class, () ->
				migrationService.migrate(""));

		// Verify
		assertThat(ex.getMessage()).isEqualTo("Error fetching file from the FTP server: Error report");
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationDirectory();
		verify(ftpProperties, times(1)).getServerCca3SectorAssociationMigrationSourceFile();
		verify(ftpFileService, times(1)).fetchFile(filePath);
		verifyNoInteractions(updateService, validationService);
	}

	@Test
	void getResource() {
		assertThat(migrationService.getResource()).isEqualTo("cca3-sector-associations");
	}
}