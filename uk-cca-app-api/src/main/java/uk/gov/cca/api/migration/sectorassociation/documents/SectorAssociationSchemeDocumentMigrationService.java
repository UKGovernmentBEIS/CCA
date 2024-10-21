package uk.gov.cca.api.migration.sectorassociation.documents;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static uk.gov.netz.api.files.common.domain.FileStatus.PENDING_MIGRATION;
import static uk.gov.netz.api.files.common.domain.FileStatus.SUBMITTED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.netz.api.common.utils.MimeTypeUtils;;

@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeDocumentMigrationService extends MigrationBaseService {

    private final FtpProperties ftpProperties;
    private final FtpFileService ftpFileService;
    
    private final SectorAssociationRepository sectorAssociationRepository;
    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationSchemeDocumentRepository sectorAssociationSchemeDocumentRepository;

    @Override
    public String getResource() {
        return "sector-association-scheme-documents";
    }

    @Override
    public List<String> migrate(String ids) {
        final List<String> results = new ArrayList<>();
        final AtomicInteger failedCounter = new AtomicInteger(0);
        
        List<SectorAssociation> sectors = sectorAssociationRepository.findAll();
        
        if(StringUtils.isNotEmpty(ids)) {
            Set<String> acronyms = Arrays.stream(ids.split(",")).collect(Collectors.toSet());
            sectors = sectors.stream().filter(sector -> acronyms.contains(sector.getAcronym())).toList();
        }
        
        sectors.forEach(sector -> results.addAll(migrateUmbrellaAgreementDocument(sector.getId(), sector.getAcronym(), failedCounter)));
        
        results.add("migration of umbrella agreement documents: " + failedCounter + "/" + sectors.size() + " failed");
        
        return results;
    }

    public List<String> migrateUmbrellaAgreementDocument(final Long sectorId, final String sectorAcronym, final AtomicInteger failedCounter) {
        final List<String> results = new ArrayList<>();
        try {

            SectorAssociationScheme scheme = sectorAssociationSchemeRepository
                    .findSectorAssociationSchemeBySectorAssociationId(sectorId)
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            
            SectorAssociationSchemeDocument umbrellaAgreementDocument = scheme.getUmbrellaAgreement();

            if (!PENDING_MIGRATION.equals(umbrellaAgreementDocument.getStatus())) {
                failedCounter.incrementAndGet();
                results.add(String.format("ERROR: migration of umbrella agreement document (%s) ignored for sector %s",
                        umbrellaAgreementDocument.getFileName(), sectorAcronym));
                return results;
            }

            // download document
            final String filePath = ftpProperties.getServerSectorUmbrellaAgreementsDirectory() + "/" + umbrellaAgreementDocument.getFileName();
            final FtpFileDTOResult file = ftpFileService.fetchFile(filePath);
            
            if (file.getErrorReport() != null) {
                failedCounter.incrementAndGet();
                results.add(String.format("ERROR: migration of umbrella agreement document failed for sector %s with %s"
                        , sectorAcronym, file.getErrorReport()));
                return results;
            }

            // create document
            SectorAssociationSchemeDocument document = createFileDocumentWithUuid(
                    file.getFileDTO().getFileContent(),
                    umbrellaAgreementDocument.getFileName(), 
                    umbrellaAgreementDocument.getUuid().toLowerCase(),
                    umbrellaAgreementDocument.getId());

            sectorAssociationSchemeDocumentRepository.save(document);
            
            results.add(String.format("Migration of umbrella agreement document (%s) succeeded for sector %s",
                    umbrellaAgreementDocument.getFileName(), sectorAcronym));

        } catch (Exception e) {
            failedCounter.incrementAndGet();
            results.add(String.format("ERROR: migration of umbrella agreement document failed for sector %s with %s"
                    , sectorAcronym, ExceptionUtils.getRootCause(e).getMessage()));
            log.error("migration of umbrella agreement document failed for sector {} with {}",
                    sectorAcronym, ExceptionUtils.getRootCause(e).getMessage());
        }

        return results;

    }

    private SectorAssociationSchemeDocument createFileDocumentWithUuid(byte[] fileContent, String fileName, String uuid, Long id) {
        return SectorAssociationSchemeDocument.builder().fileName(fileName)
                .fileContent(fileContent)
                .fileType(MimeTypeUtils.detect(fileContent, fileName))
                .fileSize(fileContent.length)
                .uuid(uuid)
                .status(SUBMITTED)
                .createdBy("migration_process")
                .id(id)
                .build();
    }
}
