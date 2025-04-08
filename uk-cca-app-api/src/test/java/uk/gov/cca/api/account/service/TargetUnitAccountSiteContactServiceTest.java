package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
public class TargetUnitAccountSiteContactServiceTest {

    @Mock
    private TargetUnitAccountRepository targetUnitAccountRepository;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;
    
    @Mock
    SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Mock
    private SectorUserAuthorityService sectorUserAuthorityService;

    @InjectMocks
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    private AppUser user;

    @BeforeEach
    public void setUp() {
        user = AppUser.builder().roleType(REGULATOR).authorities(
            List.of(AppAuthority.builder()
                .build()))
            .build();
    }

    @Test
    void findCASiteContactByAccount_null(){
        final Long accountId = 1L;
        final Long sectorAssociationId = 1L;

        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId))
                .thenReturn(sectorAssociationId);
        when(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId))
                .thenReturn(null);

        Optional<String> actual = targetUnitAccountSiteContactService.findCASiteContactByAccount(accountId);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    void findCASiteContactByAccount(){
        final Long accountId = 1L;
        final Long sectorAssociationId = 1L;

        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorAssociationId);
        when(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId)).thenReturn("facilidatorId");

        Optional<String> actual = targetUnitAccountSiteContactService.findCASiteContactByAccount(accountId);

        assertEquals(Optional.of("facilidatorId"), actual);
    }



    @Test
    void getTargetUnitAccountsWithSiteContact() {
        final Integer page = 0;
        final Integer pageSize = 25;
        final Long sectorAssociationId = 1L;
        
        List<TargetUnitAccountInfoDTO> contacts = List.of(
            new TargetUnitAccountInfoDTO(1L, "ACC-T00001", "Account name1", TargetUnitAccountStatus.LIVE, "userId1"),
            new TargetUnitAccountInfoDTO(2L, "ACC-T00002", "Account name2", TargetUnitAccountStatus.LIVE, "userId2"));
        
        Page<TargetUnitAccountInfoDTO> pagedAccounts = new PageImpl<>(contacts, PageRequest.of(0, 2), contacts.size());
        
        when(targetUnitAccountRepository.findTargetUnitAccountsWithSiteContact(PageRequest.of(page, pageSize), sectorAssociationId, CcaAccountContactType.TU_SITE_CONTACT))
        	.thenReturn(pagedAccounts);
        when(sectorAssociationAuthorizationResourceService.hasUserScopeToSectorAssociation(user, CcaScope.EDIT_SECTOR_ASSOCIATION, sectorAssociationId))
            .thenReturn(true);

        TargetUnitAccountInfoResponseDTO
            response = targetUnitAccountSiteContactService.getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, page, pageSize);

        assertEquals(pagedAccounts.getContent(), response.getAccountsWithSiteContact());
        assertEquals(pagedAccounts.getTotalElements(), response.getTotalItems());
        assertTrue(response.isEditable());

        verify(sectorAssociationAuthorizationResourceService).hasUserScopeToSectorAssociation(user, CcaScope.EDIT_SECTOR_ASSOCIATION, sectorAssociationId);
    }

    @Test
    @Transactional
    void updateTargetUnitAccountSiteContacts() {
    	Long sectorAssociationId = 1L;
        List<TargetUnitAccountSiteContactDTO> siteContacts = List.of(
            TargetUnitAccountSiteContactDTO.builder().accountId(1L).userId("userId1").build(),
            TargetUnitAccountSiteContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationId(any()))
            .thenReturn(List.of(1L, 2L));
        when(sectorUserAuthorityService.findActiveSectorUsersBySectorAssociationId(any()))
            .thenReturn(List.of("userId1", "userId2"));

        targetUnitAccountSiteContactService.updateTargetUnitAccountSiteContacts(user, sectorAssociationId, siteContacts);

        verify(targetUnitAccountQueryService).getAllTargetUnitAccountIdsBySectorAssociationId(any());
        verify(sectorUserAuthorityService).findActiveSectorUsersBySectorAssociationId(any());
    }

    @Test
    @Transactional
    void removeUserFromTargetUnitAccountSiteContact() {
        String userId = "userId1";
        Long sectorAssociationId = 1L;
        TargetUnitAccount account = mock(TargetUnitAccount.class);
        List<TargetUnitAccount> accounts = List.of(account);

        when(targetUnitAccountRepository.findTargetUnitAccountsByContactTypeAndUserIdAndSectorAsssociationId(CcaAccountContactType.TU_SITE_CONTACT, userId, sectorAssociationId))
            .thenReturn(accounts);

        targetUnitAccountSiteContactService.removeUserFromTargetUnitAccountSiteContact(userId, sectorAssociationId);

        verify(account, times(1)).getContacts();
    }

    private AppUser createOperatorUser() {

        AppCcaAuthority authority1 = new AppCcaAuthority();
        authority1.setAccountId(1L);
        authority1.setPermissions(List.of("PERMISSION_READ"));

        AppCcaAuthority authority2 = new AppCcaAuthority();
        authority2.setAccountId(2L);
        authority2.setPermissions(List.of("OTHER_PERMISSION"));

        return AppUser.builder().roleType(OPERATOR).authorities(List.of(authority1, authority2)).build();
    }

    @Test
    void getTargetUnitAccountsWithSiteContact_OperatorUser() {
        final Integer page = 0;
        final Integer pageSize = 25;
        final Long sectorAssociationId = 1L;
        final AppUser operatorUser = createOperatorUser();

        List<TargetUnitAccountInfoDTO> contacts = List.of(
                new TargetUnitAccountInfoDTO(1L, "ACC-T00001", "Account name1", TargetUnitAccountStatus.LIVE, "userId1"),
                new TargetUnitAccountInfoDTO(2L, "ACC-T00002", "Account name2", TargetUnitAccountStatus.LIVE, "userId2"));

        Page<TargetUnitAccountInfoDTO> pagedAccounts = new PageImpl<>(contacts, PageRequest.of(0, 2), contacts.size());

        when(targetUnitAccountRepository.findTargetUnitAccountWithSiteContactAndAccountsIds(PageRequest.of(page, pageSize), sectorAssociationId, operatorUser.getAccounts(), CcaAccountContactType.TU_SITE_CONTACT))
                .thenReturn(pagedAccounts);
        when(sectorAssociationAuthorizationResourceService.hasUserScopeToSectorAssociation(operatorUser, CcaScope.EDIT_SECTOR_ASSOCIATION, sectorAssociationId))
                .thenReturn(false);

        TargetUnitAccountInfoResponseDTO
                response = targetUnitAccountSiteContactService.getTargetUnitAccountsWithSiteContact(operatorUser, sectorAssociationId, page, pageSize);

        assertEquals(pagedAccounts.getContent(), response.getAccountsWithSiteContact());
        assertEquals(pagedAccounts.getTotalElements(), response.getTotalItems());

        verify(sectorAssociationAuthorizationResourceService).hasUserScopeToSectorAssociation(operatorUser, CcaScope.EDIT_SECTOR_ASSOCIATION, sectorAssociationId);
    }
}
