package uk.gov.cca.api.migration.underlyingagreement.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.files.FileMigrationError;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.documents.domain.FileDocument;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FileDocumentsMigrationService extends MigrationBaseService {
    
    private final FileDocumentMigrationRepository fileDocumentMigrationRepository;
    private final FileDocumentMigrationService fileDocumentMigrationService;
    
    private static final int BATCH_SIZE = 500;
    
    @Override
    public String getResource() {
        return "underlying-agreement-documents";
    }

    @Override
    public List<String> migrate(String fileUuids) {
        List<String> failedEntries = new ArrayList<>();

        List<String> uuidsToMigrate = collectFileDocumentUuidsToMigrate(fileUuids);

        List<FileMigrationError> migrationErrors = processInBatches(uuidsToMigrate);

        migrationErrors.forEach(error -> failedEntries.add(buildErrorMessage(error)));

        failedEntries.add("Statistics: Total: " + uuidsToMigrate.size() + ". Failed: " + failedEntries.size());
        return failedEntries;
    }
    
    private List<String> collectFileDocumentUuidsToMigrate(String uuids) {
        List<String> uuidsToMigrate = !StringUtils.isBlank(uuids)
                ? new ArrayList<>(Arrays.asList(uuids.split("\\s*,\\s*")))
                : new ArrayList<>();

        if (uuidsToMigrate.isEmpty()) {
            uuidsToMigrate = fileDocumentMigrationRepository.findByStatus(FileStatus.PENDING_MIGRATION).stream()
                    .map(FileDocument::getUuid)
                    .toList();
        }
        return uuidsToMigrate;
    }
    
    private List<FileMigrationError> processInBatches(List<String> uuidsToMigrate) {
        List<FileMigrationError> migrationErrors = new ArrayList<>();
        
        if(CollectionUtils.isEmpty(uuidsToMigrate)) {
            return migrationErrors;
        }
        
        int totalSize = uuidsToMigrate.size();
        for(int i=0; i<totalSize; i+=BATCH_SIZE) {
            Set<String> batch = new HashSet<>(uuidsToMigrate.subList(i, Math.min(i + BATCH_SIZE, totalSize)));
            migrationErrors.addAll(fileDocumentMigrationService.migrateFileDocuments(batch));
        }
        
        return migrationErrors;
    }
    
    private String buildErrorMessage(FileMigrationError error) {
        return String.format("Document UUID: %s | name: %s | Error message: %s"
                , error.getUuid(), error.getFileName(), error.getErrorReport());
    }
}
