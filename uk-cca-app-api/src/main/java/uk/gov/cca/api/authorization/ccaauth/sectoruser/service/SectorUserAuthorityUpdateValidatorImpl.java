package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SectorUserAuthorityUpdateValidatorImpl implements SectorUserAuthorityUpdateValidator {

    private final CcaAuthorityRepository ccaAuthorityRepository;
    private static final String SECTOR_USER_ADMIN_CODE = "sector_user_administrator";

    @Override
    public void validateUpdate(List<SectorUserAuthorityUpdateDTO> sectorUserAuthorityUpdateDTOS, Long sectorAssociationId) {
        if(ObjectUtils.isEmpty(sectorUserAuthorityUpdateDTOS)) {
            throw new BusinessException(ErrorCode.PARAMETERS_VALIDATION);
        }

        validateUniqueAdministrator(sectorUserAuthorityUpdateDTOS, sectorAssociationId);
        validateStatus(sectorUserAuthorityUpdateDTOS, sectorAssociationId);
    }

    private boolean isActiveSectorUserAdminSelected(List<SectorUserAuthorityUpdateDTO> userAuthorities) {
        return userAuthorities
                .stream()
                .anyMatch(sectorAuthority ->
                        SECTOR_USER_ADMIN_CODE.equalsIgnoreCase(sectorAuthority.getRoleCode()) &&
                                AuthorityStatus.ACTIVE == sectorAuthority.getAuthorityStatus()
                );
    }

    public void validateUniqueAdministrator(List<SectorUserAuthorityUpdateDTO> userAuthorities, Long associationSectorId) {
        if (!isActiveSectorUserAdminSelected(userAuthorities)) {
            List<String> currentActiveSectorAdminsUserIds = ccaAuthorityRepository.findActiveSectorUsersBySectorAndRole(associationSectorId, SECTOR_USER_ADMIN_CODE);
            List<String> currentActiveSectorAdminsToBeUpdated = userAuthorities
                    .stream()
                    .filter((userSectorAuthority) -> currentActiveSectorAdminsUserIds.contains(userSectorAuthority.getUserId())
                            && (!SECTOR_USER_ADMIN_CODE.equalsIgnoreCase(userSectorAuthority.getRoleCode())
                            || AuthorityStatus.ACTIVE != userSectorAuthority.getAuthorityStatus())).map(SectorUserAuthorityUpdateDTO::getUserId).collect(Collectors.toList());
            if (currentActiveSectorAdminsToBeUpdated.containsAll(currentActiveSectorAdminsUserIds)) {
                throw new BusinessException(CcaErrorCode.AUTHORITY_MIN_ONE_SECTOR_ADMIN_SHOULD_EXIST);
            }
        }

    }

    public void validateStatus(List<SectorUserAuthorityUpdateDTO> userAuthorities, Long associationSectorId) {
        List<String> userIds = userAuthorities.stream().map(SectorUserAuthorityUpdateDTO::getUserId).collect(Collectors.toList());
        Map<String, AuthorityStatus> userStatuses = ccaAuthorityRepository.findStatusByUsersAndSectorAssociationId(userIds, associationSectorId);
        userAuthorities.forEach((userSectorAuthority) -> {
            if (userStatuses.containsKey(userSectorAuthority.getUserId())
                    && (userStatuses.get(userSectorAuthority.getUserId())).equals(AuthorityStatus.ACCEPTED)
                    && !userSectorAuthority.getAuthorityStatus().equals(AuthorityStatus.ACTIVE)
                    && !userSectorAuthority.getAuthorityStatus().equals(AuthorityStatus.ACCEPTED)) {
                throw new BusinessException(ErrorCode.AUTHORITY_INVALID_STATUS, userSectorAuthority.getUserId(), userSectorAuthority.getAuthorityStatus().name());
            }
        });
    }

}
