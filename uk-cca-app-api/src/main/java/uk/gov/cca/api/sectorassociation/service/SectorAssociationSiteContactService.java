package uk.gov.cca.api.sectorassociation.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoResponse;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectorAssociationSiteContactService {

    private final SectorAssociationRepository sectorAssociationRepository;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    public SectorAssociationSiteContactInfoResponse getSectorAssociationSiteContacts(AppUser user, Integer page,
                                                                                     Integer pageSize) {

        Page<SectorAssociationSiteContactInfoDTO>
            contacts = sectorAssociationRepository.findSectorAssociationsSiteContactsByCA(
            user.getCompetentAuthority(), PageRequest.of(page, pageSize));

        boolean isEditable = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(user, Scope.EDIT_USER);

        return SectorAssociationSiteContactInfoResponse.builder()
            .siteContacts(contacts.get().collect(Collectors.toList()))
            .totalItems(contacts.getTotalElements())
            .editable(isEditable)
            .build();
    }

    @Transactional
    public void updateSectorAssociationSiteContacts(AppUser user, List<SectorAssociationSiteContactDTO> siteContactsUpdate) {

        CompetentAuthorityEnum ca = user.getCompetentAuthority();

        var collectedResults = siteContactsUpdate.stream()
            .map(dto -> new SimpleEntry<>(dto.getSectorAssociationId(), dto.getUserId()))
            .collect(Collectors.teeing(
                Collectors.mapping(SimpleEntry::getKey, Collectors.toSet()),
                Collectors.mapping(SimpleEntry::getValue, Collectors.toSet()),
                SimpleEntry::new
            ));

        Set<Long> sectorIdsUpdate = collectedResults.getKey();
        Set<String> userIdsUpdate = collectedResults.getValue();

        validateSectorAssociationsByCA(sectorIdsUpdate, ca);
        validateActiveRegulatorsByCA(userIdsUpdate, ca);

        doUpdateSectorAssociationsSiteContacts(siteContactsUpdate);
    }

    @Transactional
    public void removeUserFromSectorAssociationSiteContact(String userId) {
        List<SectorAssociation> sectorAssociationList = sectorAssociationRepository.findAllByFacilitatorUserId(userId);
        sectorAssociationList
            .forEach(sa -> sa.setFacilitatorUserId(null));
    }

    private void doUpdateSectorAssociationsSiteContacts(List<SectorAssociationSiteContactDTO> siteContactsUpdate) {
        List<Long> sectorAssociationsIdsUpdate =
            siteContactsUpdate.stream()
                .map(SectorAssociationSiteContactDTO::getSectorAssociationId)
                .collect(Collectors.toList());
        List<SectorAssociation> sectorAssociations = sectorAssociationRepository.findAllByIdIn(sectorAssociationsIdsUpdate);

        siteContactsUpdate
            .forEach(contact -> sectorAssociations.stream()
                .filter(sa -> sa.getId().equals(contact.getSectorAssociationId()))
                .findFirst()
                .ifPresent(ac -> {
                    ac.setFacilitatorUserId(contact.getUserId());
                }));
    }

    /** Validates that sectors exists and belongs to CA */
    private void validateSectorAssociationsByCA(Set<Long> sectorAssociationIds, CompetentAuthorityEnum ca) {
        List<Long> sectors = sectorAssociationRepository.findSectorAssociationsIdsByCompetentAuthority(ca);
        if (!sectors.containsAll(sectorAssociationIds)) {
            throw new BusinessException(CcaErrorCode.SECTOR_ASSOCIATION_NOT_RELATED_TO_CA);
        }
    }

    /** Validates that active regulator users exists and belongs to CA */
    private void validateActiveRegulatorsByCA(Set<String> userIds, CompetentAuthorityEnum ca) {
        Set<String> filteredUserIds = userIds.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (filteredUserIds.isEmpty()) {
            return;
        }

        List<String> users = regulatorAuthorityResourceService.findUsersByCompetentAuthority(ca);
        if (!users.containsAll(filteredUserIds)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA);
        }
    }
}
