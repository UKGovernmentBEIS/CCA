package uk.gov.cca.api.sectorassociation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeDocumentMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileValidatorService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.UserFileTokenService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeDocumentService {

    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationSchemeDocumentRepository sectorAssociationSchemeDocumentRepository;
    private final UserFileTokenService userFileTokenService;
    private final SectorAssociationSchemeDocumentMapper sectorAssociationSchemeDocumentMapper;
    private final List<FileValidatorService> fileValidators;
    private final DateService dateService;

    private static final int DAYS_CUTOFF = 1;
    private static final Set<String> ALLOWED_FILE_TYPES = FileType.PDF.getMimeTypes();

    @Transactional
    public String createSectorAssociationSchemeDocument(@Valid FileDTO fileDTO, AppUser appUser) {

        if (!ALLOWED_FILE_TYPES.contains(fileDTO.getFileType())) {
            throw new BusinessException(CcaErrorCode.INVALID_SECTOR_ASSOCIATION_SCHEME_UPLOAD_FILE_TYPE);
        }

        fileValidators.forEach(validator -> validator.validate(fileDTO));

        SectorAssociationSchemeDocument sectorAssociationSchemeDocument = sectorAssociationSchemeDocumentMapper.toSectorAssociationSchemeDocument(fileDTO);
        sectorAssociationSchemeDocument.setUuid(UUID.randomUUID().toString());
        sectorAssociationSchemeDocument.setStatus(FileStatus.PENDING);
        sectorAssociationSchemeDocument.setCreatedBy(appUser.getUserId());

        sectorAssociationSchemeDocumentRepository.save(sectorAssociationSchemeDocument);

        return sectorAssociationSchemeDocument.getUuid();
    }

    @Transactional(readOnly = true)
    public FileToken generateDocumentFileToken(Long sectorId, UUID documentUuid, AppUser submitter) {
        SectorAssociationSchemeDocument sectorAssociationSchemeDocument = sectorAssociationSchemeDocumentRepository.findByUuid(documentUuid.toString())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        if (sectorAssociationSchemeDocument.getStatus().equals(FileStatus.PENDING)) {
            // check that the file has been created by the submitter
            if (submitter.getUserId().equals(sectorAssociationSchemeDocument.getCreatedBy())) {
                return userFileTokenService.generateGetFileToken(documentUuid.toString());
            } else {
                throw new BusinessException(CcaErrorCode.SUBMITTER_HAS_NO_ACCESS_TO_SCHEME_DOCUMENT);
            }
        }

        validateDocumentFileToken(sectorId, documentUuid);
        return userFileTokenService.generateGetFileToken(documentUuid.toString());
    }

    @Transactional(readOnly = true)
    public FileDTO getSectorAssociationSchemeDocumentDTOByToken(String getFileToken) {
        String documentUuid = userFileTokenService.resolveGetFileUuid(getFileToken);
        return sectorAssociationSchemeDocumentRepository.findByUuid(documentUuid)
                .map(sectorAssociationSchemeDocumentMapper::sectorAssociationSchemeDocumentToFileDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public SectorAssociationSchemeDocument getSectorAssociationSchemeDocumentByUuid(String uuid) {

        return sectorAssociationSchemeDocumentRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, uuid));
    }

    @Transactional
    public void submitSectorAssociationSchemeDocument(String uuid) {
        SectorAssociationSchemeDocument sectorAssociationSchemeDocument = sectorAssociationSchemeDocumentRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        sectorAssociationSchemeDocument.setStatus(FileStatus.SUBMITTED);
    }

    public void cleanUpUnusedFiles() {
        final LocalDateTime expirationDate = dateService.getLocalDateTime().minusDays(DAYS_CUTOFF);
        sectorAssociationSchemeDocumentRepository.deleteSchemeDocumentsByStatusAndDateBefore(FileStatus.PENDING, expirationDate);
    }

    private void validateDocumentFileToken(Long sectorId, UUID documentUuid) {
        List<SectorAssociationScheme> sectorAssociationSchemes =
                sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorId);

        List<String> schemeUuids = sectorAssociationSchemes.stream()
                .map(scheme -> scheme.getUmbrellaAgreement().getUuid())
                .toList();
        String uuid = documentUuid.toString();
        if (!schemeUuids.contains(uuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, documentUuid);
        }
    }
}
