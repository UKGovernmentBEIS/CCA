package uk.gov.cca.api.migration.facilitycertification;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FacilityCertificationMigrationService extends MigrationBaseService {
    
    private static final FacilityCertificationMigrationMapper MIGRATION_MAPPER = Mappers.getMapper(FacilityCertificationMigrationMapper.class);
    
    private final FtpProperties ftpProperties;
    private final FtpFileService ftpFileService;
    private final FacilityCertificationMigrationValidationService validationService;
    private final FacilityCertificationService facilityCertificationService;
    
    @Override
    public String getResource() {
        return "facility-certification";
    }
    
    @Override
    public List<String> migrate(String input) {
        String csvInput = getCsvInputFromFtpServer();
        
        AtomicInteger failCounter = new AtomicInteger(0);
        
        FacilityCertificationMigrationParseResult parseResult = FacilityCertificationParser.parse(csvInput, failCounter);
        List<String> allErrors = new ArrayList<>(parseResult.getParsingErrors());
        
        List<FacilityCertificationVO> facilityCertificationVOList = parseResult.getParsedfacilityCertificationVOList();
        
        facilityCertificationVOList.forEach(vo -> processFacilityCertification(vo, failCounter, allErrors));
        
        if (failCounter.get() > 0) {
            allErrors.add(String.format("Migration failed for %d/%d record(s).",
                    failCounter.get(), parseResult.getTotalRecords()));
        }
        
        return allErrors;
    }
    
    private @NotNull String getCsvInputFromFtpServer() {
        final String filePath = ftpProperties.getServerFacilityCertificationDirectory() + "/"
                + ftpProperties.getServerFacilityCertificationSourceFile();
        
        final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);
        if (fileDTOResult.getErrorReport() != null) {
            throw new FtpFileGenericException("Error fetching file from the FTP server: " + fileDTOResult.getErrorReport());
        }
        
        FileDTO fileDTO = fileDTOResult.getFileDTO();
        
        return new String(fileDTO.getFileContent(), StandardCharsets.UTF_8);
    }
    
    private void processFacilityCertification(FacilityCertificationVO vo, AtomicInteger failCounter, List<String> allErrors) {
        List<String> errors = validationService.validate(vo);
        
        if (errors.isEmpty()) {
            try {
                FacilityCertificationDTO dto = MIGRATION_MAPPER.toFacilityCertificationDTO(vo);
                facilityCertificationService.saveFacilityCertification(dto);
            } catch (Exception e) {
                failCounter.incrementAndGet();
                allErrors.add(
                        String.format("Failed to save Facility with ID %s, reason: %s",
                                vo.getFacilityBusinessId(), e.getLocalizedMessage()));
            }
        } else {
            allErrors.addAll(errors);
            failCounter.incrementAndGet();
        }
    }
}
