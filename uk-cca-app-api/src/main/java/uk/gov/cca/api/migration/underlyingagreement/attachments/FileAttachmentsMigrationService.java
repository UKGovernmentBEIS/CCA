package uk.gov.cca.api.migration.underlyingagreement.attachments;

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
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.common.domain.FileStatus;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FileAttachmentsMigrationService extends MigrationBaseService {
    
    private final FileAttachmentMigrationRepository fileAttachmentMigrationRepository;
    private final FileAttachmentMigrationService fileAttachmentMigrationService;
    
    private static final int BATCH_SIZE = 500;
    
    @Override
    public String getResource() {
        return "underlying-agreement-attachments";
    }

    @Override
    public List<String> migrate(String fileUuids) {
        List<String> failedEntries = new ArrayList<>();

        List<String> uuidsToMigrate = collectFileAttachmentUuidsToMigrate(fileUuids);

        List<FileMigrationError> migrationErrors = processInBatches(uuidsToMigrate);

        migrationErrors.forEach(error -> failedEntries.add(buildErrorMessage(error)));

        failedEntries.add("Statistics: Total: " + uuidsToMigrate.size() + ". Failed: " + failedEntries.size());
        return failedEntries;
    }
    
    private List<String> collectFileAttachmentUuidsToMigrate(String uuids) {
        List<String> uuidsToMigrate = !StringUtils.isBlank(uuids)
                ? new ArrayList<>(Arrays.asList(uuids.split("\\s*,\\s*")))
                : new ArrayList<>();

        if (uuidsToMigrate.isEmpty()) {
            uuidsToMigrate = fileAttachmentMigrationRepository.findByStatus(FileStatus.PENDING_MIGRATION).stream()
                    .map(FileAttachment::getUuid)
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
            migrationErrors.addAll(fileAttachmentMigrationService.migrateFileAttachments(batch));
        }
        
        return migrationErrors;
    }
    
    private String buildErrorMessage(FileMigrationError error) {
        return String.format("Attachment UUID: %s | name: %s | Error message: %s"
                , error.getUuid(), error.getFileName(), error.getErrorReport());
    }
}
