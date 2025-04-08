package uk.gov.cca.api.migration.underlyingagreement.attachments;

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
public class FileAttachmentMetadataMigrationService extends MigrationBaseService {
    
    private final FileAttachmentMigrationService fileAttachmentMigrationService;
    private final FileAttachmentMigrationRepository fileAttachmentRepository;
    
    @Override
    public String getResource() {
        return "underlying-agreement-attachments-metadata";
    }

    @Override
    public List<String> migrate(String ids) {
        List<String> migrationResults = new ArrayList<>();

        try {
            final List<String> files = fileAttachmentMigrationService.listUnderlyingAgreementAttachments();

            fileAttachmentMigrationService.saveFileAttachmentMetadata(files);

            long persistentFilesCount = fileAttachmentRepository.countAllByStatus(PENDING_MIGRATION);
            migrationResults.add("Underlying agreement attachments loaded: " + persistentFilesCount + "/" + files.size());
            
        } catch (Exception e) {
            migrationResults.add("ERROR: migration of underlying agreement attachments metadata failed with "
                    + ExceptionUtils.getRootCause(e).getMessage());
        }

        return migrationResults;
    }

}
