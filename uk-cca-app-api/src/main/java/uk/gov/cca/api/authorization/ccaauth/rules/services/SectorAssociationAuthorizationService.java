package uk.gov.cca.api.authorization.ccaauth.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SectorAssociationAuthorizationService {

    private final SectorAssociationAuthorizationServiceDelegator authorizationService;

    public void authorize(AppUser user, Long sectorAssociationId, String permission) {
        final boolean isAuthorized;
        if (permission == null) {
            isAuthorized = authorizationService.isAuthorized(user, sectorAssociationId);
        } else {
            isAuthorized = authorizationService.isAuthorized(user, sectorAssociationId, permission);
        }

        if(!isAuthorized) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
