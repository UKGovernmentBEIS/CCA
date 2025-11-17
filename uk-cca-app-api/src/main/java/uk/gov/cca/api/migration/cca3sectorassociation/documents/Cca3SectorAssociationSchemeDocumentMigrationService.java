package uk.gov.cca.api.migration.cca3sectorassociation.documents;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class Cca3SectorAssociationSchemeDocumentMigrationService  extends MigrationBaseService {

	private final SectorAssociationSchemeDocumentRepository schemeDocumentRepository;
	private final Cca3SectorAssociationSchemeDocumentUpdateMigrationService updateMigrationService;

	@Override
	public List<String> migrate(String ids) {
		final List<String> results = new ArrayList<>();
		final AtomicInteger failedCounter = new AtomicInteger(0);

		List<SectorAssociationSchemeDocument> documents = schemeDocumentRepository.findAll();

		documents.forEach(document ->
				results.addAll(updateMigrationService
						.updateDocument(document.getUuid(), document.getFileName(), failedCounter)));

		results.add("migration of umbrella agreement documents: " + failedCounter.get() + "/" + documents.size() + " failed");

		return results;
	}

	@Override
	public String getResource() {
		return "cca3-sector-association-scheme-documents";
	}
}
