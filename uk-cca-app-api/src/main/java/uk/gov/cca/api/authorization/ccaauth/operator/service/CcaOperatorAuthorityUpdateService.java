package uk.gov.cca.api.authorization.ccaauth.operator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityConstants;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityUpdateValidator;

@Service
public class CcaOperatorAuthorityUpdateService {

    public CcaOperatorAuthorityUpdateService(@Qualifier("operatorStatusModificationAllowanceValidator") OperatorAuthorityUpdateValidator operatorAuthorityUpdateValidator,
                                             CcaOperatorAuthorityService ccaOperatorAuthorityService,
                                             AuthorityRepository authorityRepository) {
        this.operatorAuthorityUpdateValidator = operatorAuthorityUpdateValidator;
        this.ccaOperatorAuthorityService = ccaOperatorAuthorityService;
        this.authorityRepository = authorityRepository;
    }

    private final OperatorAuthorityUpdateValidator operatorAuthorityUpdateValidator;
    private final CcaOperatorAuthorityService ccaOperatorAuthorityService;
    private final AuthorityRepository authorityRepository;

    @Transactional
    public List<NewUserActivated> updateAccountOperatorAuthorities(List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities,
                                                                   Long accountId) {
        if (accountOperatorAuthorities.isEmpty()) {
            return new ArrayList<>();
        }

        operatorAuthorityUpdateValidator.validateUpdate(accountOperatorAuthorities, accountId);

        // Update authorities
        List<NewUserActivated> notifyUsers = new ArrayList<>();
        accountOperatorAuthorities.forEach(au ->
                updateAccountOperatorUserAuthority(au, accountId).ifPresent((notifyUsers::add))
        );

        return notifyUsers;
    }

    private Optional<NewUserActivated> updateAccountOperatorUserAuthority(AccountOperatorAuthorityUpdateDTO operatorUserUpdate, Long accountId) {
        Authority authority = ccaOperatorAuthorityService.getOperatorUserAuthorityByUserIdAndAccountId(operatorUserUpdate.getUserId(), accountId);

        AuthorityStatus previousStatus = authority.getStatus();

        if (previousStatus.equals(operatorUserUpdate.getAuthorityStatus())) {
            return Optional.empty();
        }

        // Update status
        authority.setStatus(operatorUserUpdate.getAuthorityStatus());
        authorityRepository.save(authority);

        // Add notification message for enable user from accepted invitation
        if (AuthorityStatus.ACCEPTED.equals(previousStatus) && AuthorityStatus.ACTIVE.equals(operatorUserUpdate.getAuthorityStatus())) {
            return Optional.of(NewUserActivated
                    .builder()
                    .userId(operatorUserUpdate.getUserId())
                    .accountId(accountId)
                    .roleCode(CcaAuthorityConstants.OPERATOR_ROLE_CODE)
                    .build()
            );
        }

        return Optional.empty();
    }
    
    /**
     * Updates operator user Details without contactType
     * @param userId
     * @param accountId
     * @param organisationName
     */
    @Transactional
    public void updateOperatorUserAuthorityDetails(String userId, Long accountId, String organisationName) {
        final CcaAuthorityDetails authorityDetails = ccaOperatorAuthorityService.getOperatorUserAuthorityDetails(userId, accountId);
        authorityDetails.setOrganisationName(organisationName);
    }

    /**
     * Updates operator user Details plus contactType
     * @param userId
     * @param accountId
     * @param organisationName
     * @param contactType
     */
    @Transactional
    public void updateOperatorUserAuthorityDetailsWithContactType(String userId, Long accountId, String organisationName, ContactType contactType) {
        final CcaAuthorityDetails authorityDetails = ccaOperatorAuthorityService.getOperatorUserAuthorityDetails(userId, accountId);
        authorityDetails.setOrganisationName(organisationName);
        authorityDetails.setContactType(contactType);
    }
}
