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

import java.util.List;
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
        List<SectorAssociationScheme> sectorAssociationSchemes =
                sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorId);

        List<String> schemeUuids = sectorAssociationSchemes.stream()
        		.map(scheme -> scheme.getUmbrellaAgreement().getUuid())
        		.toList();
        String uuid = documentUuid.toString();
        if (!schemeUuids.contains(uuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, documentUuid);
        }

        if (!sectorAssociationSchemeDocumentRepository.existsByUuid(uuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, uuid);
        }
    }
}
