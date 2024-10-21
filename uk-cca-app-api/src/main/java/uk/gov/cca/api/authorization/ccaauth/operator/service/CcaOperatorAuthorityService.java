package uk.gov.cca.api.authorization.ccaauth.operator.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityAssignmentService;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.authorization.ccaauth.operator.event.CcaOperatorAuthorityDeletionEvent;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.UuidGenerator;

import java.util.List;
import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT;

@Log4j2
@Service
@AllArgsConstructor
public class CcaOperatorAuthorityService {

    private final CcaAuthorityAssignmentService authorityAssignmentService;
    private final AuthorityRepository authorityRepository;
    private final CcaAuthorityDetailsRepository authorityDetailsRepository;
    private final CcaAuthorityService authorityService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String createPendingAuthorityForOperator(Long accountId, String roleCode, ContactType contactType, String userId, AppUser authModificationUser) {

        Optional<Authority> userAuthorityForAccount =
                this.authorityRepository.findByUserIdAndAccountId(userId, accountId);

        if (userAuthorityForAccount.isPresent()) {
            Authority authority = userAuthorityForAccount.get();
            if (AuthorityStatus.PENDING.equals(authority.getStatus())) {
                final CcaAuthorityDetails authorityDetails = authorityService.getAuthorityDetails(authority.getId());
                return this.authorityAssignmentService.updatePendingAuthority(authorityDetails, roleCode, contactType, authModificationUser.getUserId());
            } else {
                log.warn("Authority for user '{}' in target unit '{}' exists with code '{}' and status'{}'",
                        authority.getUserId(), authority.getAccountId(), authority.getCode(), authority.getStatus());
                throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
            }
        }

        return this.createOperatorUserAuthorityForRole(accountId, roleCode, contactType, userId, authModificationUser.getUserId());

    }

    public CcaAuthorityDetails getOperatorUserAuthorityDetails(String userId, Long accountId){
        Authority authority = getOperatorUserAuthorityByUserIdAndAccountId(userId, accountId);
        return authorityService.getAuthorityDetails(authority.getId());
    }

    @Transactional
    public void deleteAccountOperatorAuthority(String userId, Long accountId){
        List<Authority> authorities = authorityRepository.findByUserId(userId);
        Authority authority = getAuthority(accountId, authorities);

        authorityDetailsRepository.deleteById(authority.getId());
        eventPublisher.publishEvent(CcaOperatorAuthorityDeletionEvent.builder()
                .userId(userId)
                .accountId(accountId)
                .existAuthoritiesOnOtherAccounts(authorities.stream().anyMatch(auth -> !accountId.equals(auth.getAccountId())))
                .build());
    }

    private static Authority getAuthority(Long accountId, List<Authority> authorities) {
        return authorities.stream().filter(auth -> accountId.equals(auth.getAccountId()))
                .findFirst().orElseThrow(() -> new BusinessException(AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT));
    }

    public Authority getOperatorUserAuthorityByUserIdAndAccountId(String userId, Long accountId){
	    return authorityRepository.findByUserIdAndAccountId(userId, accountId).
	            orElseThrow(() -> new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT));
    }

    private Role getRoleByCode(String roleCode) {
        return this.roleRepository.findByCode(roleCode).orElseThrow(() ->
                new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String createOperatorUserAuthorityForRole(Long accountId, String roleCode, ContactType contactType, String userId, String createdByUserId) {
        final Authority authority = Authority.builder()
                .userId(userId)
                .code(roleCode)
                .accountId(accountId)
                .status(AuthorityStatus.PENDING)
                .createdBy(createdByUserId)
                .uuid(UuidGenerator.generate())
                .build();

        final CcaAuthorityDetails authorityDetails = CcaAuthorityDetails.builder()
                .contactType(contactType)
                .authority(authority).build();

        final Role role = this.getRoleByCode(roleCode);

        return this.authorityAssignmentService.createAuthorityInfoForRole(authorityDetails, role);
    }
}
