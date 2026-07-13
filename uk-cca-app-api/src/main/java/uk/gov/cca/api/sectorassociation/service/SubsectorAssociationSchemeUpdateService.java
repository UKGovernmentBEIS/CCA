package uk.gov.cca.api.sectorassociation.service;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentUpdateDTO;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubsectorAssociationSchemeUpdateService {

    private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;

    private static final List<SchemeVersion> EXCLUDED_SCHEME_VERSIONS = List.of(SchemeVersion.CCA_2);

    @Transactional
    public void updateSubsectorAssociationSchemeTargetCommitments(Long subsectorAssociationSchemeId,
                                                                  @Valid @NonNull TargetCommitmentsUpdateDTO targetCommitmentsUpdateDTO) {

        SubsectorAssociationScheme subsectorAssociationScheme = validateAndGetSubsectorAssociationScheme(subsectorAssociationSchemeId);

        Map<Long, BigDecimal> updatedTargetMap = targetCommitmentsUpdateDTO.getTargetCommitments().stream()
                .collect(Collectors.toMap(TargetCommitmentUpdateDTO::getId, TargetCommitmentUpdateDTO::getTargetImprovement));

        subsectorAssociationScheme.getTargetSet().getTargetCommitments().forEach(targetCommitment -> {
            if (!updatedTargetMap.containsKey(targetCommitment.getId())) {
                throw new BusinessException(CcaErrorCode.TARGET_COMMITMENT_NOT_RELATED_TO_SECTOR_SCHEME);
            }
            BigDecimal updatedImprovement = updatedTargetMap.get(targetCommitment.getId()).divide(new BigDecimal("100"), 5, RoundingMode.HALF_DOWN);
            targetCommitment.setTargetImprovement(updatedImprovement);
        });
    }

    private SubsectorAssociationScheme validateAndGetSubsectorAssociationScheme(Long subsectorAssociationSchemeId) {
        SubsectorAssociationScheme subsectorAssociationScheme = subsectorAssociationSchemeRepository.findById(subsectorAssociationSchemeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (EXCLUDED_SCHEME_VERSIONS.contains(subsectorAssociationScheme.getSchemeVersion())) {
            throw new BusinessException(CcaErrorCode.INVALID_SUBSECTOR_ASSOCIATION_SCHEME_VERSION);
        }

        return subsectorAssociationScheme;
    }
}
