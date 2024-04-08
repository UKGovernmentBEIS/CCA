package uk.gov.cca.api.account.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.enumeration.AccountContactType;
import uk.gov.cca.api.authorization.AuthorityConstants;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecondaryContactValidator implements AccountContactTypeUpdateValidator {

    private final AuthorityService authorityService;

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        String userId = contactTypes.get(AccountContactType.SECONDARY);
        if (userId != null) {
            Optional<AuthorityInfoDTO> userAccountAuthorityOptional =
                authorityService.findAuthorityByUserIdAndAccountId(userId, accountId);

            userAccountAuthorityOptional.ifPresent(userAccountAuthority -> {
                if (AuthorityConstants.OPERATOR_ROLE_CODE.equals(userAccountAuthority.getCode())) {
                    throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SECONDARY_CONTACT_NOT_OPERATOR);
                }
            });
        }
    }
}
