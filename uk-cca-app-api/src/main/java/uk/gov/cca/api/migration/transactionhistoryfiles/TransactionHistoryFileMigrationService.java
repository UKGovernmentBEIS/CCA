package uk.gov.cca.api.migration.transactionhistoryfiles;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.cca.api.files.evidences.repository.FileEvidenceRepository;
import uk.gov.cca.api.files.evidences.transform.FileEvidenceMapper;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileValidatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class TransactionHistoryFileMigrationService {

    private final FileEvidenceRepository fileEvidenceRepository;
    private final FtpFileService ftpService;
    private final List<FileValidatorService> fileValidators;

    private static final FileEvidenceMapper MAPPER = Mappers.getMapper(FileEvidenceMapper.class);
    private static final String ERROR_FORMAT = "%s: %s";
    private static final String FILENAME_SEPARATOR = "_";

    @Transactional
    public List<String> transferFilesToDatabase(List<String> files) {
        List<String> errors = new ArrayList<>();
        List<FileEvidence> evidences = new ArrayList<>();

        ftpService.fetchFiles(files).forEach(ftpFileDTOResult -> {
            if(ftpFileDTOResult.getErrorReport() != null) {
                errors.add(String.format(ERROR_FORMAT, ftpFileDTOResult.getFileDTO().getFileName(), ftpFileDTOResult.getErrorReport()));
            }
            else {
                Optional.ofNullable(createFileEvidence(ftpFileDTOResult.getFileDTO(), errors)).ifPresent(evidences::add);
            }
        });

        if (!evidences.isEmpty()) {
            fileEvidenceRepository.saveAll(evidences);
        }

        return errors;
    }

    private FileEvidence createFileEvidence(FileDTO fileDTO, List<String> errors) {
        List<String> validationErrors = new ArrayList<>();

        this.fileValidators.forEach(validator -> {
            try {
                validator.validate(fileDTO);
            } catch (RuntimeException e) {
                validationErrors.add(e.getMessage());
            }
        });

        if (!validationErrors.isEmpty()) {
            validationErrors.forEach(error -> errors.add(String.format(ERROR_FORMAT, fileDTO.getFileName(), error)));
            return null;
        }

        try {
            int separatorIndex = fileDTO.getFileName().indexOf(FILENAME_SEPARATOR);
            if(separatorIndex <= 0) {
                errors.add(String.format(ERROR_FORMAT, fileDTO.getFileName(), "Filename has no " + FILENAME_SEPARATOR));
                return null;
            }

            String uuid = fileDTO.getFileName().substring(0, separatorIndex);
            String fileName = fileDTO.getFileName().substring(separatorIndex + 1);

            FileEvidence evidence = MAPPER.toFileEvidence(fileDTO);
            evidence.setFileName(fileName);
            evidence.setUuid(uuid);
            evidence.setStatus(FileStatus.SUBMITTED);

            return evidence;
        } catch (Exception e) {
            errors.add(String.format(ERROR_FORMAT, fileDTO.getFileName(), e.getMessage()));
            return null;
        }
    }
}
