package uk.gov.cca.api.migration.cca3sectorassociation.documents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.files.FileValidatorMigrationService;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationSchemeDocumentUpdateMigrationServiceTest {

	@InjectMocks
	private Cca3SectorAssociationSchemeDocumentUpdateMigrationService service;

	@Mock
	private FtpProperties ftpProperties;

	@Mock
	private FtpFileService ftpFileService;

	@Mock
	private SectorAssociationSchemeDocumentRepository schemeDocumentRepository;

	@Mock
	private FileValidatorMigrationService fileValidatorMigrationService;
	private static final String FORWARD_SLASH = "/";

	@Test
	void updateDocument() throws Exception {
		SectorAssociationSchemeDocument document = SectorAssociationSchemeDocument.builder()
				.fileName("file")
				.createdBy("createdBy")
				.id(1L)
				.fileSize(100L)
				.fileContent("fileContent".getBytes())
				.fileType("fileType")
				.uuid("uuid")
				.status(FileStatus.SUBMITTED)
				.build();

		final FileDTO fileDTO = FileDTO.builder()
				.fileContent("fileContent200".getBytes())
				.fileType("fileType200")
				.fileSize("fileContent200".getBytes().length)
				.build();
		final FtpFileDTOResult ftpFileDTOResult = FtpFileDTOResult.builder()
				.fileDTO(fileDTO)
				.build();
		final String filePath = "serverCca3SectorUmbrellaAgreementsDirectory" + FORWARD_SLASH + document.getFileName();
		when(ftpProperties.getServerCca3SectorUmbrellaAgreementsDirectory())
				.thenReturn("serverCca3SectorUmbrellaAgreementsDirectory");
		when(ftpFileService.fetchFile(filePath))
				.thenReturn(ftpFileDTOResult);
		when(schemeDocumentRepository.findByUuid(document.getUuid()))
				.thenReturn(Optional.of(document));

		List<String> result = service.updateDocument(document.getUuid(), document.getFileName(), new AtomicInteger(0));

		verify(fileValidatorMigrationService, times(1))
				.validateFileDTO(fileDTO);
		verify(ftpFileService, times(1))
				.fetchFile(filePath);
		assertEquals(0, result.size());
		assertEquals(fileDTO.getFileContent(), document.getFileContent());
		assertEquals(fileDTO.getFileType(), document.getFileType());
		assertEquals(fileDTO.getFileSize(), document.getFileSize());
		assertEquals(MigrationConstants.MIGRATION_PROCESS_USER, document.getCreatedBy());
	}



	@Test
	void updateDocument_fails_errorReport() {
		SectorAssociationSchemeDocument document = SectorAssociationSchemeDocument.builder()
				.fileName("fileName")
				.createdBy("createdBy")
				.id(1L)
				.fileSize(100L)
				.fileContent("fileContent".getBytes())
				.fileType("fileType")
				.uuid("uuid")
				.status(FileStatus.PENDING)
				.build();

		final FileDTO fileDTO = FileDTO.builder().build();
		final FtpFileDTOResult ftpFileDTOResult = FtpFileDTOResult.builder()
				.fileDTO(fileDTO)
				.errorReport("errorReport")
				.build();
		final String filePath = "serverCca3SectorUmbrellaAgreementsDirectory" + FORWARD_SLASH + document.getFileName();

		when(ftpProperties.getServerCca3SectorUmbrellaAgreementsDirectory())
				.thenReturn("serverCca3SectorUmbrellaAgreementsDirectory");
		when(ftpFileService.fetchFile(filePath))
				.thenReturn(ftpFileDTOResult);

		List<String> result = service.updateDocument(document.getUuid(), document.getFileName(), new AtomicInteger(0));

		verify(ftpFileService, times(1))
				.fetchFile(filePath);
		assertEquals(1, result.size());
		assertTrue(result.getFirst().contains("errorReport"));
	}

	@Test
	void updateDocument_fails() {
		SectorAssociationSchemeDocument document = SectorAssociationSchemeDocument.builder()
				.fileName("fileName")
				.createdBy("createdBy")
				.id(1L)
				.fileSize(100L)
				.fileContent("fileContent".getBytes())
				.fileType("fileType")
				.uuid("uuid")
				.status(FileStatus.SUBMITTED)
				.build();

		final FileDTO fileDTO = FileDTO.builder()
				.fileName("fileName")
				.build();
		final FtpFileDTOResult ftpFileDTOResult = FtpFileDTOResult.builder()
				.fileDTO(fileDTO)
				.errorReport("errorReport")
				.build();
		final String filePath = "serverCca3SectorUmbrellaAgreementsDirectory" + FORWARD_SLASH + document.getFileName();

		when(ftpProperties.getServerCca3SectorUmbrellaAgreementsDirectory())
				.thenReturn("serverCca3SectorUmbrellaAgreementsDirectory");
		when(ftpFileService.fetchFile(filePath))
				.thenReturn(ftpFileDTOResult);

		List<String> result = service.updateDocument(document.getUuid(), document.getFileName(), new AtomicInteger(0));

		verify(ftpFileService, times(1))
				.fetchFile(filePath);
		assertEquals(1, result.size());
		assertTrue(result.getFirst().contains("ERROR: File \"fileName\" failed with"));
	}
}