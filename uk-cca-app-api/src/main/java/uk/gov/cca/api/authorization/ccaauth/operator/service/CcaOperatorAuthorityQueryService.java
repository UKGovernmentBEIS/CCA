package uk.gov.cca.api.authorization.ccaauth.operator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.AccountAuthorizationResourceService;

import java.util.List;

@Service
@AllArgsConstructor
public class CcaOperatorAuthorityQueryService {

    private final AccountAuthorizationResourceService accountAuthorizationResourceService;
    private final CcaAuthorityRepository ccaAuthorityRepository;

    public OperatorAuthoritiesDTO getOperatorAuthorities(AppUser appUser, Long accountId) {
        boolean isEditable = accountAuthorizationResourceService
                .hasUserScopeToAccount(appUser, accountId, Scope.EDIT_USER);

        List<OperatorAuthorityDTO> operatorAuthorityDTOList = ccaAuthorityRepository.findAuthoritiesWithDetailsByAccountId(accountId);

        return OperatorAuthoritiesDTO.builder().authorities(operatorAuthorityDTOList).editable(isEditable).build();
    }
}
