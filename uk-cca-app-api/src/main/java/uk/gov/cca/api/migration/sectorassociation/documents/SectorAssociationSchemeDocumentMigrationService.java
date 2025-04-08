package uk.gov.cca.api.migration.sectorassociation.documents;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.files.FileValidatorMigrationService;
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
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static uk.gov.netz.api.files.common.domain.FileStatus.PENDING_MIGRATION;
import static uk.gov.netz.api.files.common.domain.FileStatus.SUBMITTED;


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
    
    private final FileValidatorMigrationService fileValidatorMigrationService;

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
            sectors = sectors.stream()
                    .filter(sector -> acronyms.contains(sector.getAcronym()))
                    .toList();
        }
        
        sectors.forEach(sector -> results.addAll(migrateUmbrellaAgreementDocument(sector.getId(), sector.getAcronym(), failedCounter)));
        
        results.add("migration of umbrella agreement documents: " + failedCounter + "/" + sectors.size() + " failed");
        
        return results;
    }

    @Transactional
    public List<String> migrateUmbrellaAgreementDocument(final Long sectorId, final String sectorAcronym, final AtomicInteger failedCounter) {
        final List<String> results = new ArrayList<>();
        
        try {
            SectorAssociationScheme scheme = sectorAssociationSchemeRepository
                    .findSectorAssociationSchemeBySectorAssociationId(sectorId)
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            
            SectorAssociationSchemeDocument schemeDocument = scheme.getUmbrellaAgreement();

            if (!PENDING_MIGRATION.equals(schemeDocument.getStatus())) {
                failedCounter.incrementAndGet();
                results.add(String.format("WARNING: File %s is not in pending migration mode", schemeDocument.getFileName()));
                return results;
            }

            //Download document
            final String filePath = ftpProperties.getServerSectorUmbrellaAgreementsDirectory() + "/" + schemeDocument.getFileName();
            final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);
            
            if (fileDTOResult.getErrorReport() != null) {
                failedCounter.incrementAndGet();
                results.add(String.format("ERROR: File failed for sector %s with %s", sectorAcronym, fileDTOResult.getErrorReport()));
                return results;
            }
            
            FileDTO fileDTO = fileDTOResult.getFileDTO();

            //Validate document
            fileValidatorMigrationService.validateFileDTO(fileDTO);

            //Create document
            createSectorAssociationSchemeDocument(fileDTO, schemeDocument.getUuid().toLowerCase(), schemeDocument.getId());
            
            results.add(String.format("File %s succeeded for sector %s", schemeDocument.getFileName(), sectorAcronym));

        } catch (Exception e) {
            failedCounter.incrementAndGet();
            results.add(String.format("ERROR: File failed for sector %s with %s"
                    , sectorAcronym, ExceptionUtils.getRootCause(e).getMessage()));
            log.error("File failed for sector {} with {}",
                    sectorAcronym, ExceptionUtils.getRootCause(e).getMessage());
        }

        return results;
    }

    private void createSectorAssociationSchemeDocument(FileDTO fileDTO, String uuid, Long id) {
        SectorAssociationSchemeDocument fileDocument = SectorAssociationSchemeDocument.builder()
                .fileName(fileDTO.getFileName())
                .fileContent(fileDTO.getFileContent())
                .fileType(fileDTO.getFileType())
                .fileSize(fileDTO.getFileContent().length)
                .uuid(uuid)
                .status(SUBMITTED)
                .createdBy(MigrationConstants.MIGRATION_PROCESS_USER)
                .id(id)
                .build();

        sectorAssociationSchemeDocumentRepository.save(fileDocument);
    }
}
