package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.utils.CsvUtils;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3SectorAssociationMigrationService extends MigrationBaseService {

	private final FtpProperties ftpProperties;
	private final FtpFileService ftpFileService;
	private final Cca3SectorAssociationMigrationValidationService validationService;
	private final Cca3SectorAssociationMigrationUpdateService updateService;

	@Override
	public List<String> migrate(String input) {
		List<String> errors = new ArrayList<>();

		// Get csv from server
		FileDTO fileDTO = getCsvInputFromFtpServer();

		// Parse CSV
		List<Cca3SectorAssociationVO> cca3SectorAssociationVOList = CsvUtils
				.convertToModel(fileDTO, Cca3SectorAssociationVO.class, true, errors);

		// Validate
		validationService.validate(cca3SectorAssociationVOList, errors);

		if(errors.isEmpty()) {
			try {
				updateService.updateSectorAssociationDataList(cca3SectorAssociationVOList);
			} catch (Exception e) {
				errors.add(ExceptionUtils.getRootCause(e).getMessage());
			}
		}
		return errors;
	}

	@Override
	public String getResource() {
		return "cca3-sector-associations";
	}

	private FileDTO getCsvInputFromFtpServer() {
		final String filePath = ftpProperties.getServerCca3SectorAssociationMigrationDirectory() + "/"
				+ ftpProperties.getServerCca3SectorAssociationMigrationSourceFile();

		final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);
		if (fileDTOResult.getErrorReport() != null) {
			throw new FtpFileGenericException("Error fetching file from the FTP server: " + fileDTOResult.getErrorReport());
		}

		return fileDTOResult.getFileDTO();
	}
}
