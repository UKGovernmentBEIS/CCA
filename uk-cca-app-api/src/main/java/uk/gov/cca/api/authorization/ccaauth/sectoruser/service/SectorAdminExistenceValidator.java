package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class SectorAdminExistenceValidator implements SectorAuthorityDeleteValidator {

    private final CcaAuthorityRepository ccaAuthorityRepository;

    @Override
    public void validateDeletion(Authority authority) {
        if ("sector_user_administrator".equals(authority.getCode()) && AuthorityStatus.ACTIVE.equals(authority.getStatus()) && !this.ccaAuthorityRepository.existsOtherSectorUserAdmin(authority.getUserId())) {
            throw new BusinessException(CcaErrorCode.AUTHORITY_MIN_ONE_SECTOR_ADMIN_SHOULD_EXIST);
        }
    }
}
