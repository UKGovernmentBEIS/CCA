package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserRegisterValidationService {

    private final SectorAuthorityQueryService sectorAuthorityQueryService;
    private final UserRoleTypeService userRoleTypeService;

    public void validateRegister(final String userId) {
        if(sectorAuthorityQueryService.existsAuthorityNotForSectorAssociation(userId)) {
            throw new BusinessException(CcaErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_SECTOR_USER);
        }

        userRoleTypeService.validateUserRoleTypeIsOfTypeOrNotExist(userId, SECTOR_USER);
    }

    public void validateRegisterForSectorAssociation(final String userId, final Long sectorAssociationId) {
        validateRegister(userId);

        if (sectorAuthorityQueryService.existsNonPendingAuthorityForSectorAssociation(userId, sectorAssociationId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
        }
    }

}
