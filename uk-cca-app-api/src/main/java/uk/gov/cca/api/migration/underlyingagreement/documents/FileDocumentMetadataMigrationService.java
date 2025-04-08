package uk.gov.cca.api.migration.underlyingagreement.documents;

import static uk.gov.netz.api.files.common.domain.FileStatus.PENDING_MIGRATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.netz.api.common.utils.ExceptionUtils;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FileDocumentMetadataMigrationService extends MigrationBaseService {
    
    private final FileDocumentMigrationService fileDocumentMigrationService;
    private final FileDocumentMigrationRepository fileDocumentRepository;
    
    @Override
    public String getResource() {
        return "underlying-agreement-documents-metadata";
    }

    @Override
    public List<String> migrate(String ids) {
        List<String> migrationResults = new ArrayList<>();
        
        try {
            List<String> files = fileDocumentMigrationService.listUnaDocuments();
            
            fileDocumentMigrationService.saveFileDocumentMetadata(files);
            
            long persistentFilesCount = fileDocumentRepository.countAllByStatus(PENDING_MIGRATION);
            migrationResults.add("Underlying agreement documents loaded: " + persistentFilesCount + "/" + files.size());
            
        } catch (Exception e) {
            migrationResults.add("ERROR: migration of underlying agreement documents metadata failed with "
                    + ExceptionUtils.getRootCause(e).getMessage());
        }
        
        return migrationResults;
    }

}
