package uk.gov.cca.api.migration.cca3sectorassociation.documents;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.files.FileValidatorMigrationService;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class Cca3SectorAssociationSchemeDocumentUpdateMigrationService {

	private final FtpProperties ftpProperties;
	private final FtpFileService ftpFileService;
	private final FileValidatorMigrationService fileValidatorMigrationService;
	private final SectorAssociationSchemeDocumentRepository schemeDocumentRepository;
	private static final String FORWARD_SLASH = "/";

	@Transactional
	public List<String> updateDocument(String documentUuid, String documentFilename, AtomicInteger failedCounter) {
		List<String> results = new ArrayList<>();
		try {

			//Download document
			final String filePath = ftpProperties.getServerCca3SectorUmbrellaAgreementsDirectory() + FORWARD_SLASH + documentFilename;

			final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);

			if (fileDTOResult.getErrorReport() != null) {
				throw new FtpFileGenericException(fileDTOResult.getErrorReport());
			}

			FileDTO fileDTO = fileDTOResult.getFileDTO();

			//Validate document
			fileValidatorMigrationService.validateFileDTO(fileDTO);

			SectorAssociationSchemeDocument schemeDocument = schemeDocumentRepository.findByUuid(documentUuid)
					.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

			// Update document
			schemeDocument.setFileContent(fileDTO.getFileContent());
			schemeDocument.setFileSize(fileDTO.getFileContent().length);
			schemeDocument.setFileType(fileDTO.getFileType());
			schemeDocument.setCreatedBy(MigrationConstants.MIGRATION_PROCESS_USER);

		} catch (Exception e) {
			failedCounter.incrementAndGet();
			results.add(String.format("ERROR: File \"%s\" failed with %s", documentFilename,
					ExceptionUtils.getRootCause(e).getMessage()));
			log.error("File \"%s\" failed {} with {}", documentFilename,
					ExceptionUtils.getRootCause(e).getMessage());
		}
		return results;
	}
}
