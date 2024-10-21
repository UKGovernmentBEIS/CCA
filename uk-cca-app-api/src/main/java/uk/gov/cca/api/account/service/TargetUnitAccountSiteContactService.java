package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.account.service.AccountCaSiteContactProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class TargetUnitAccountSiteContactService implements AccountCaSiteContactProvider {

    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final SectorUserAuthorityService sectorUserAuthorityService;
    private final SectorAssociationQueryService sectorAssociationQueryService;


    @Override
    public Optional<String> findCASiteContactByAccount(Long accountId) {
        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);

        return Optional.ofNullable(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId));
    }


    public TargetUnitAccountInfoResponseDTO getTargetUnitAccountsWithSiteContact(AppUser user, Long sectorAssociationId, final Integer page, final Integer pageSize) {
    	
        Page<TargetUnitAccountInfoDTO> accountWithSiteContactPage =
        		getTargetUnitAccountDtosWithSiteContact(sectorAssociationId, CcaAccountContactType.TU_SITE_CONTACT, page, pageSize);

        boolean isEditable = sectorAssociationAuthorizationResourceService
                .hasUserScopeToSectorAssociation(user, CcaScope.EDIT_SECTOR_ASSOCIATION, sectorAssociationId);

        // Transform properly
        return TargetUnitAccountInfoResponseDTO.builder()
            .accountsWithSiteContact(accountWithSiteContactPage.toList())
            .totalItems(accountWithSiteContactPage.getTotalElements())
            .editable(isEditable)
            .build();
    }

    private Page<TargetUnitAccountInfoDTO> getTargetUnitAccountDtosWithSiteContact(Long sectorAssociationId, String siteContactType, final Integer page, final Integer pageSize) {
        return targetUnitAccountRepository.findTargetUnitAccountsWithSiteContact(
            PageRequest.of(page, pageSize),
            sectorAssociationId,
            siteContactType
        );
    }

    @Transactional
    public void removeUserFromTargetUnitAccountSiteContact(String userId, Long sectorAssociationId) {
		List<TargetUnitAccount> accounts = targetUnitAccountRepository
				.findTargetUnitAccountsByContactTypeAndUserIdAndSectorAsssociationId(CcaAccountContactType.TU_SITE_CONTACT, userId, sectorAssociationId);
        
        accounts.forEach(ac -> ac.getContacts().remove(CcaAccountContactType.TU_SITE_CONTACT));
    }

    @Transactional
    public void updateTargetUnitAccountSiteContacts(AppUser user, Long sectorAssociationId, List<TargetUnitAccountSiteContactDTO> siteContacts) {

        // Validate accounts belonging to Sector Association
        Set<Long> accountIds =
        		siteContacts.stream()
        					.map(TargetUnitAccountSiteContactDTO::getAccountId)
        					.collect(Collectors.toSet());
        validateTargetUnitAccountsBySectorAssociationId(accountIds, sectorAssociationId);

        // Validate users belonging to Sector Association
        Set<String> userIds = siteContacts.stream()
        								  .map(TargetUnitAccountSiteContactDTO::getUserId)
        								  .filter(Objects::nonNull)
        								  .collect(Collectors.toSet());
        validateActiveSectorUsersBySectorAssociationId(userIds, sectorAssociationId);

        // Update contacts in DB
        doUpdateTargetUnitAccountSiteContacts(siteContacts);
    }

    private void doUpdateTargetUnitAccountSiteContacts(List<TargetUnitAccountSiteContactDTO> siteContactsToUpdate) {
        List<Long> accountIdsToUpdate =
        		siteContactsToUpdate.stream()
                				    .map(TargetUnitAccountSiteContactDTO::getAccountId)
                				    .toList();
        List<TargetUnitAccount> accounts = targetUnitAccountQueryService.getAccounts(accountIdsToUpdate);

        siteContactsToUpdate
            .forEach(contact -> accounts.stream()
                                        .filter(ac -> ac.getId().equals(contact.getAccountId()))
                                        .findFirst()
                                        .ifPresent(ac -> {
                                        	ac.getContacts().put(CcaAccountContactType.TU_SITE_CONTACT, contact.getUserId()); }
                                        )
                                     );
    }

    /** Validates that target unit account exists and belongs to sector association */
    private void validateTargetUnitAccountsBySectorAssociationId(Set<Long> accountIds, Long sectorAssociationId) {
    	
        List<Long> accounts = targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationId(sectorAssociationId);
        if(!accounts.containsAll(accountIds)){
            throw new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        }
    }

    /** Validates that sector user exists and belongs to sector association and has active status */
    private void validateActiveSectorUsersBySectorAssociationId(Set<String> userIds, Long sectorAssociationId) {

        List<String> users = sectorUserAuthorityService.findActiveSectorUsersBySectorAssociationId(sectorAssociationId);
        if (!users.containsAll(userIds)) {
            throw new BusinessException(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        }
    }

    public Optional<String> findTargetUnitAccountSiteContactByAccountId(Long accountId) {
        Optional<TargetUnitAccount> accountOpt = targetUnitAccountRepository.findById(accountId);
        return accountOpt
                .map(TargetUnitAccount::getContacts)
                .map(contacts -> contacts.get(CcaAccountContactType.TU_SITE_CONTACT));
    }
}
