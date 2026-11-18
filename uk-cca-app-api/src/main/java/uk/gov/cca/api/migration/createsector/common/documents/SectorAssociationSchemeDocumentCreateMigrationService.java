package uk.gov.cca.api.migration.createsector.common.documents;

import static uk.gov.netz.api.files.common.domain.FileStatus.PENDING_MIGRATION;
import static uk.gov.netz.api.files.common.domain.FileStatus.SUBMITTED;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.files.FileValidatorMigrationService;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeDocumentCreateMigrationService {

	private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
	private final SectorAssociationSchemeDocumentRepository sectorAssociationSchemeDocumentRepository;
	private final FileValidatorMigrationService fileValidatorMigrationService;
	private final FtpProperties ftpProperties;
    private final FtpFileService ftpFileService;
    
    private static final String FORWARD_SLASH = "/";
    
	@Transactional
    public List<String> migrateUmbrellaAgreementDocument(final Long sectorId, final String sectorAcronym, final AtomicInteger failedCounter) {
        final List<String> results = new ArrayList<>();
        
        try {
        	List<SectorAssociationScheme> schemes = sectorAssociationSchemeRepository
        	        .findSectorAssociationSchemesBySectorAssociationId(sectorId)
        	        .stream()
        	        .filter(s -> PENDING_MIGRATION.equals(s.getUmbrellaAgreement().getStatus()))
        	        .toList();

        	if (schemes.isEmpty()) {
        		failedCounter.incrementAndGet();
                results.add(String.format(
                		"WARNING: No files found in pending migration mode for sector %s", sectorAcronym));
                return results;
        	}
        	
        	if (schemes.size() > 1) {
        		failedCounter.incrementAndGet();
                results.add(String.format(
                		"WARNING: There are files in pending migration mode for multiple schemes for sector %s", sectorAcronym));
                return results;
        	}
            
            SectorAssociationSchemeDocument schemeDocument = schemes.get(0).getUmbrellaAgreement();

            //Download document
            final String filePath = ftpProperties.getServerSectorUmbrellaAgreementsDirectory() + FORWARD_SLASH + schemeDocument.getFileName();
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
