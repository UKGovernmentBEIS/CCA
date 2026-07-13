package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsectorAssociationSchemeAuthorityInfoProvider;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeInfo;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SubsectorAssociationSchemeMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubsectorAssociationSchemeService implements SubsectorAssociationSchemeAuthorityInfoProvider {

    private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    private final SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper;
    private final SubsectorAssociationService subsectorAssociationService;
    private final SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Transactional(readOnly = true)
    public SubsectorAssociationSchemesDTO getSubsectorAssociationSchemesBySubsectorAssociationId(Long subsectorId, AppUser appUser) {

        Long sectorAssociationId = subsectorAssociationService.getSectorAssociationIdBySubsectorId(subsectorId);

        boolean hasEditPermission = sectorAssociationAuthorizationResourceService.hasUserScopeToSubsectorAssociationSchemesBySectorId(appUser, CcaScope.EDIT_SECTOR_ADVANCED_DETAILS, sectorAssociationId);

        String subsectorName = subsectorAssociationService.getSubsectorById(subsectorId).getName();
        Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeViewMap = getSubsectorAssociationSchemesViewMap(subsectorId, hasEditPermission);

        return subsectorAssociationSchemeMapper.toSubsectorAssociationSchemesDTO(subsectorName, subsectorAssociationSchemeViewMap);
    }

    @Transactional(readOnly = true)
    public Map<SchemeVersion, SubsectorAssociationSchemeInfo> getSubsectorAssociationSchemesMap(Long subsectorId) {
        return subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorId)
                .stream()
                .collect(Collectors.toMap(SubsectorAssociationScheme::getSchemeVersion, subsectorAssociationSchemeMapper::toSubsectorAssociationSchemeInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSectorAssociationIdBySubsectorSchemeId(Long schemeId) {
        return subsectorAssociationSchemeRepository.findById(schemeId)
                .map(subsectorAssociationScheme -> subsectorAssociationScheme.getSubsectorAssociation().getSectorAssociation().getId())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    private Map<SchemeVersion, SubsectorAssociationSchemeDTO> getSubsectorAssociationSchemesViewMap(Long subsectorId, boolean hasEditPermission) {
        return subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorId)
                .stream()
                .collect(Collectors.toMap(SubsectorAssociationScheme::getSchemeVersion, scheme -> subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeDTO(scheme, hasEditPermission)));
    }
}
