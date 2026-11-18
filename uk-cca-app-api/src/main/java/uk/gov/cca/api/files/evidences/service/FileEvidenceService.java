package uk.gov.cca.api.files.evidences.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.cca.api.files.evidences.repository.FileEvidenceRepository;
import uk.gov.cca.api.files.evidences.transform.FileEvidenceMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileValidatorService;
import uk.gov.netz.api.files.common.transform.FileMapper;
import uk.gov.netz.api.token.UserFileTokenService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Validated
@RequiredArgsConstructor
@Log4j2
public class FileEvidenceService {

    private static final int DAYS_CUTOFF = 1;

    private final DateService dateService;
    private final FileEvidenceRepository fileEvidenceRepository;
    private final UserFileTokenService userFileTokenService;
    private final List<FileValidatorService> fileValidators;
    private static final FileEvidenceMapper fileEvidenceMapper = Mappers.getMapper(FileEvidenceMapper.class);
    private static final FileMapper fileMapper = Mappers.getMapper(FileMapper.class);

    public void cleanUpUnusedFiles() {
        final LocalDateTime expirationDate = dateService.getLocalDateTime().minusDays(DAYS_CUTOFF);
        fileEvidenceRepository.deleteEvidenceFilesByStatusAndDateBefore(FileStatus.PENDING, expirationDate);
    }

    @Transactional
    public String createFileEvidence(@Valid FileDTO fileDTO, AppUser appUser) throws IOException {

        fileValidators.forEach(validator -> validator.validate(fileDTO));

        FileEvidence evidence = fileEvidenceMapper.toFileEvidence(fileDTO);
        evidence.setUuid(UUID.randomUUID().toString());
        evidence.setStatus(FileStatus.PENDING);
        evidence.setCreatedBy(appUser.getUserId());

        fileEvidenceRepository.save(evidence);

        return evidence.getUuid();
    }

    @Transactional
    public void submitFileEvidence(String uuid) {
        FileEvidence fileEvidence = fileEvidenceRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        fileEvidence.setStatus(FileStatus.SUBMITTED);
    }

    @Transactional(readOnly = true)
    public FileDTO getFileDTOByToken(String fileEvidenceToken) {
        String fileEvidenceUuid = userFileTokenService.resolveGetFileUuid(fileEvidenceToken);
        return getFileDTO(fileEvidenceUuid);
    }

    @Transactional(readOnly = true)
    public FileDTO getFileDTO(String uuid) {

        return fileMapper.toFileDTO(getFileEvidenceByUuid(uuid));
    }

    public boolean existFileEvidence(String uuid) {

        return existsFileEvidences(Set.of(uuid));
    }

    public boolean existsFileEvidences(Set<String> uuids) {

        Set<String> nonNullUuids = uuids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (nonNullUuids.isEmpty()) {
            return true;
        }
        return nonNullUuids.size() == fileEvidenceRepository.countAllByUuidIn(nonNullUuids);
    }

    public void cleanUpUnusedNoteFilesAsync() {

        CompletableFuture.runAsync(this::cleanUpUnusedFiles)
                .exceptionally(ex -> {
                    log.error(ex);
                    return null;
                });
    }

    public FileEvidence getFileEvidenceByUuid(String uuid) {

        return fileEvidenceRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, uuid));
    }
}


