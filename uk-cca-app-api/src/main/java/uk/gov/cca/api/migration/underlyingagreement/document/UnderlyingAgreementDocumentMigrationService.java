package uk.gov.cca.api.migration.underlyingagreement.document;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationContainer;
import uk.gov.cca.api.migration.underlyingagreement.documents.FileDocumentMigrationRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.domain.FileDocument;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class UnderlyingAgreementDocumentMigrationService {
    
    private final FileDocumentMigrationRepository fileDocumentRepository;
    
    public void populate(Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap) {
        List<FileDocument> fileDocuments = fileDocumentRepository.findByStatus(FileStatus.PENDING_MIGRATION);

        migrationContainerMap.forEach((targetUnitId, migrationContainer) -> {
            List<FileDocument> documents = fileDocuments.stream()
                    .filter(doc -> doc.getFileName().startsWith(targetUnitId))
                    .filter(doc -> doc.getFileName().contains("v".concat(String.valueOf(migrationContainer.getConsolidationNumber()))))
                    .toList();
            if (CollectionUtils.emptyIfNull(documents).size() == 1) {
                migrationContainer.setFileDocument(FileInfoDTO.builder()
                        .uuid(documents.get(0).getUuid())
                        .name(documents.get(0).getFileName())
                        .build());
            }
        });
    }
}
