package uk.gov.cca.api.migration.cca3sectorassociation.documents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationSchemeDocumentMigrationServiceTest {

	@InjectMocks
	private Cca3SectorAssociationSchemeDocumentMigrationService service;

	@Mock
	private SectorAssociationSchemeDocumentRepository schemeDocumentRepository;

	@Mock
	private Cca3SectorAssociationSchemeDocumentUpdateMigrationService updateMigrationService;

	@Test
	void migrate() {
		SectorAssociationSchemeDocument document = SectorAssociationSchemeDocument.builder()
				.fileName("file")
				.createdBy("createdBy")
				.id(1L)
				.fileSize(100L)
				.fileContent("fileContent".getBytes())
				.uuid("uuid")
				.build();

		when(schemeDocumentRepository.findAll())
				.thenReturn(List.of(document));

		List<String> results = service.migrate("");

		assertEquals(1, results.size());
		assertTrue(results
				.contains("migration of umbrella agreement documents: 0/1 failed"));
	}

	@Test
	void migrate_fails() {
		SectorAssociationSchemeDocument document = SectorAssociationSchemeDocument.builder()
				.fileName("file")
				.createdBy("createdBy")
				.id(1L)
				.fileSize(100L)
				.fileContent("fileContent".getBytes())
				.uuid("uuid")
				.build();

		when(schemeDocumentRepository.findAll())
				.thenReturn(List.of(document));
		when(updateMigrationService.updateDocument(anyString(), anyString(), any()))
				.thenReturn(List.of("error"));

		List<String> results = service.migrate("");

		assertEquals(2, results.size());
		assertEquals("error", results.getFirst());
		assertEquals("migration of umbrella agreement documents: 0/1 failed", results.getLast());
	}

	@Test
	void getResource() {
		String resource = service.getResource();

		assertNotNull(resource);
		assertEquals("cca3-sector-association-scheme-documents", resource);
	}
}