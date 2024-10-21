package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeDocumentMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.UserFileTokenService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeDocumentService {

    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationSchemeDocumentRepository sectorAssociationSchemeDocumentRepository;
    private final UserFileTokenService userFileTokenService;
    private final SectorAssociationSchemeDocumentMapper sectorAssociationSchemeDocumentMapper;

    @Transactional(readOnly = true)
    public FileToken generateDocumentFileToken(Long sectorId, UUID documentUuid) {
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

    private void validateDocumentFileToken(Long sectorId, UUID documentUuid) {
        SectorAssociationScheme sectorAssociationScheme =
                sectorAssociationSchemeRepository.findSectorAssociationSchemeBySectorAssociationId(sectorId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        String uuid = documentUuid.toString();
        if (!uuid.equals(sectorAssociationScheme.getUmbrellaAgreement().getUuid())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, documentUuid);
        }

        if (!sectorAssociationSchemeDocumentRepository.existsByUuid(uuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, uuid);
        }
    }
}
