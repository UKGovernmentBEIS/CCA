package uk.gov.cca.api.sectorassociation.service;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentUpdateDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.FileStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeUpdateService {

    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;

    private static final List<SchemeVersion> EXCLUDED_SCHEME_VERSIONS = List.of(SchemeVersion.CCA_2);

    @Transactional
    public void updateSectorAssociationSchemeDetails(Long sectorAssociationSchemeId,
                                                     @Valid @NonNull SectorAssociationSchemeDetailsUpdateDTO detailsUpdateDTO) {

        SectorAssociationScheme sectorAssociationScheme = validateAndGetSectorAssociationScheme(sectorAssociationSchemeId);

        String schemeDocumentUuid = detailsUpdateDTO.getUmbrellaAgreementUuid();

        SectorAssociationSchemeDocument umbrellaAgreementDocument = sectorAssociationSchemeDocumentService.getSectorAssociationSchemeDocumentByUuid(schemeDocumentUuid);
        // submit sector association scheme document
        umbrellaAgreementDocument.setStatus(FileStatus.SUBMITTED);

        sectorAssociationScheme.setUmbrellaAgreement(umbrellaAgreementDocument);
        sectorAssociationScheme.setUmaDate(detailsUpdateDTO.getUmaDate());
        sectorAssociationScheme.setSectorDefinition(detailsUpdateDTO.getSectorDefinition());

        sectorAssociationSchemeDocumentService.cleanUpUnusedFiles();
    }

    @Transactional
    public void updateSectorAssociationSchemeTargetCommitments(Long sectorAssociationSchemeId,
                                                               @Valid @NonNull TargetCommitmentsUpdateDTO targetCommitmentsUpdateDTO) {

        SectorAssociationScheme sectorAssociationScheme = validateAndGetSectorAssociationScheme(sectorAssociationSchemeId);

        Map<Long, BigDecimal> updatedTargetMap = targetCommitmentsUpdateDTO.getTargetCommitments().stream()
                .collect(Collectors.toMap(TargetCommitmentUpdateDTO::getId, TargetCommitmentUpdateDTO::getTargetImprovement));

        sectorAssociationScheme.getTargetSet().getTargetCommitments().forEach(targetCommitment -> {
            if (!updatedTargetMap.containsKey(targetCommitment.getId())) {
                throw new BusinessException(CcaErrorCode.TARGET_COMMITMENT_NOT_RELATED_TO_SECTOR_SCHEME);
            }
            BigDecimal updatedImprovement = updatedTargetMap.get(targetCommitment.getId()).divide(new BigDecimal("100"), 5, RoundingMode.HALF_DOWN);
            targetCommitment.setTargetImprovement(updatedImprovement);
        });
    }

    private SectorAssociationScheme validateAndGetSectorAssociationScheme(Long sectorAssociationSchemeId) {
        SectorAssociationScheme sectorAssociationScheme = sectorAssociationSchemeRepository.findById(sectorAssociationSchemeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (EXCLUDED_SCHEME_VERSIONS.contains(sectorAssociationScheme.getSchemeVersion())) {
            throw new BusinessException(CcaErrorCode.INVALID_SECTOR_ASSOCIATION_SCHEME_VERSION);
        }

        return sectorAssociationScheme;
    }
}
