package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityAssignmentService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserStatusDisabledEvent;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;


@Service
@RequiredArgsConstructor
@Slf4j
public class SectorUserAuthorityUpdateService {

    private final SectorUserAuthorityService authorityService;
    private final CcaAuthorityRepository authorityRepository;
    private final CcaAuthorityAssignmentService authorityAssignmentService;
    private final CcaAuthorityDetailsRepository authorityDetailsRepository;
    private final RoleRepository roleRepository;
    private final SectorUserAuthorityUpdateValidator sectorAuthorityUpdateValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CcaAuthorityDetails updateSectorUserAuthorityDetails(Long sectorAssociationId, String userId, ContactType contactType, String organisationName, boolean updateContactType) {
        final CcaAuthorityDetails authorityDetails = authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId);
        if (updateContactType) {
            authorityDetails.setContactType(contactType);
        }
        authorityDetails.setOrganisationName(organisationName);
        // update CcaAuthorityDetails
        return authorityDetailsRepository.save(authorityDetails);
    }

    @Transactional
    public List<NewUserActivated> updateSectorUserAuthorities(List<SectorUserAuthorityUpdateDTO> sectorAuthorityUpdateDTOS, Long sectorAssociationId) {
        if (sectorAuthorityUpdateDTOS.isEmpty()) {
            return List.of();
        }
        validateRoleCodes(sectorAuthorityUpdateDTOS);
        List<NewUserActivated> notifyUsers = new ArrayList<>();
        sectorAuthorityUpdateValidator.validateUpdate(sectorAuthorityUpdateDTOS, sectorAssociationId);
        for (SectorUserAuthorityUpdateDTO toUpdateAuthority : sectorAuthorityUpdateDTOS) {
            boolean haschange = false;
            CcaAuthority existingAuthority = authorityService.getSectorUserAuthority(toUpdateAuthority.getUserId(), sectorAssociationId);

            if (existingAuthority.getStatus().equals(AuthorityStatus.ACCEPTED) && toUpdateAuthority.getAuthorityStatus().equals(AuthorityStatus.ACTIVE)) {
                notifyUsers.add(NewUserActivated.builder().userId(toUpdateAuthority.getUserId()).roleCode(toUpdateAuthority.getRoleCode()).build());
            }

            if (!ObjectUtils.isEmpty(toUpdateAuthority.getAuthorityStatus()) && toUpdateAuthority.getAuthorityStatus() != existingAuthority.getStatus()) {
                existingAuthority.setStatus(toUpdateAuthority.getAuthorityStatus());
                haschange = true;
            }

            if (!ObjectUtils.isEmpty(toUpdateAuthority.getRoleCode()) && !toUpdateAuthority.getRoleCode().equals(existingAuthority.getCode())) {
                String newRoleCode = toUpdateAuthority.getRoleCode();
                Optional<Role> newRoleOptional = roleRepository.findByCode(newRoleCode);
				newRoleOptional.ifPresentOrElse(
						role -> this.authorityAssignmentService.assignAuthorityWithNewRole(existingAuthority, role),
						() -> log.error("Role not found for code: " + newRoleCode));
                haschange = true;
            }

            if (haschange) {
                authorityRepository.save(existingAuthority);
            }
        }

        publishDisabledSectorUser(sectorAuthorityUpdateDTOS, sectorAssociationId);
        return notifyUsers;

    }

    private void publishDisabledSectorUser(List<SectorUserAuthorityUpdateDTO> sectorAuthorityUpdateDTOS,
                                           final Long sectorAssociationId) {
        List<String> disabledSectorUserAuthorityIds = sectorAuthorityUpdateDTOS
                .stream().filter(ru -> AuthorityStatus.DISABLED == ru.getAuthorityStatus())
                .map(SectorUserAuthorityUpdateDTO::getUserId)
                .toList();
        if (ObjectUtils.isNotEmpty(disabledSectorUserAuthorityIds)) {
            disabledSectorUserAuthorityIds
                    .forEach(ru -> eventPublisher.publishEvent(SectorUserStatusDisabledEvent.builder()
                            .userId(ru)
                            .sectorAssociationId(sectorAssociationId)
                            .build()));
        }
    }

    private void validateRoleCodes(List<SectorUserAuthorityUpdateDTO> sectorUsers) {
        Set<String> newRoleCodes = sectorUsers.stream().map(SectorUserAuthorityUpdateDTO::getRoleCode).collect(Collectors.toSet());
        Set<String> sectorUserRoleCodes = this.roleRepository.findByType(SECTOR_USER).stream().map(Role::getCode).collect(Collectors.toSet());
        if (!sectorUserRoleCodes.containsAll(newRoleCodes)) {
            List<String> invalidCodes = new ArrayList(newRoleCodes);
            invalidCodes.removeAll(sectorUserRoleCodes);
            throw new BusinessException(CcaErrorCode.ROLE_INVALID_SECTOR_USER_ROLE_CODE, new Object[]{invalidCodes});
        }
    }
}
