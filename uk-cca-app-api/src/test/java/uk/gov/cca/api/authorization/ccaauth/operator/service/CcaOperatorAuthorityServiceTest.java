package uk.gov.cca.api.authorization.ccaauth.operator.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT;


@ExtendWith(MockitoExtension.class)
class CcaOperatorAuthorityServiceTest {

    @InjectMocks
    private CcaOperatorAuthorityService operatorAuthorityService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private CcaAuthorityDetailsRepository authorityDetailsRepository;
    
    @Mock
    private CcaAuthorityService authorityService;

    @Mock
    private CcaAuthorityAssignmentService authorityAssignmentService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Authority authority;
    private String userId = "testUserId";
    private Long accountId = 123L;

    @BeforeEach
    public void setUp() {
        userId = "testUserId";
        accountId = 1L;
        authority = new Authority();
        authority.setAccountId(accountId);
        authority.setId(1L);
    }


    @Test
    void createPendingAuthorityForOperator_pending_authority_exists() {
        Long accountId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        String authorityUuid = "uuid";
        AppUser modificationUser = AppUser.builder().userId("current_user_id").build();
        Authority existingAuthority = createAuthority(userId, roleCode, accountId, AuthorityStatus.PENDING, authorityUuid);
        CcaAuthorityDetails existingAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(existingAuthority)
                .contactType(ContactType.OPERATOR)
                .build();

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId))
                .thenReturn(Optional.of(existingAuthority));

        when(authorityService.getAuthorityDetails(existingAuthority.getId())).thenReturn(existingAuthorityDetails);

        when(authorityAssignmentService.updatePendingAuthority(existingAuthorityDetails, roleCode, ContactType.OPERATOR, modificationUser.getUserId()))
                .thenReturn(authorityUuid);

        String result = operatorAuthorityService
                .createPendingAuthorityForOperator(accountId, roleCode, ContactType.OPERATOR, userId, modificationUser);

        assertThat(result).isEqualTo(authorityUuid);

        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(roleRepository, never()).findByCode(anyString());
        verify(authorityAssignmentService, times(1))
                .updatePendingAuthority(existingAuthorityDetails, roleCode, ContactType.OPERATOR, modificationUser.getUserId());
    }

    @Test
    void createPendingAuthorityForOperator_throws_exception_when_active_authority_exists() {
        Long accountId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        Authority existingAuthority = createAuthority(userId, "anotherRoleCode", accountId, AuthorityStatus.ACTIVE, "authorityUuid");

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(existingAuthority));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, ContactType.OPERATOR, userId, currentUser));

        assertEquals(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED, businessException.getErrorCode());

        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(authorityRepository, never()).save(any());
        verify(roleRepository, never()).findByCode(anyString());
    }

    @Test
    void createPendingAuthorityForOperatorUser_authority_not_exists() {
        Long accountId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        Role role = Role.builder().code(roleCode).build();

        String authorityUuid = "uuid";

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.empty());
        when(roleRepository.findByCode(roleCode)).thenReturn(Optional.of(role));
        when(authorityAssignmentService.createAuthorityInfoForRole(Mockito.any(CcaAuthorityDetails.class), Mockito.eq(role)))
                .thenReturn(authorityUuid);
        String result = operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, ContactType.OPERATOR, userId, currentUser);
        assertThat(result).isEqualTo(authorityUuid);

        ArgumentCaptor<CcaAuthorityDetails> authorityCaptor = ArgumentCaptor.forClass(CcaAuthorityDetails.class);
        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(roleRepository, times(1)).findByCode(roleCode);
        verify(authorityAssignmentService, times(1))
                .createAuthorityInfoForRole(authorityCaptor.capture(), eq(role));

        Authority authoritySaved = authorityCaptor.getValue().getAuthority();

        assertThat(authoritySaved).isNotNull();
        assertThat(authoritySaved.getUserId()).isEqualTo(userId);
        assertThat(authoritySaved.getCode()).isEqualTo(role.getCode());
        assertThat(authoritySaved.getUuid()).isNotNull();
        assertThat(authoritySaved.getCreatedBy()).isEqualTo(currentUser.getUserId());
        assertThat(authoritySaved.getStatus()).isEqualTo(AuthorityStatus.PENDING);
        assertThat(authoritySaved.getAccountId()).isEqualTo(accountId);
        assertThat(authoritySaved.getVerificationBodyId()).isNull();
        assertThat(authoritySaved.getCompetentAuthority()).isNull();
    }

    private Authority createAuthority(String userId, String roleCode, Long accountId, AuthorityStatus status, String authorityUuid) {
        return Authority.builder()
                .id(1L)
                .userId(userId)
                .code(roleCode)
                .accountId(accountId)
                .status(status)
                .authorityPermissions(new ArrayList<>())
                .uuid(authorityUuid)
                .build();
    }

    @Test
    public void testDeleteAccountOperatorAuthority() {
        authority.setId(1L); // Set some dummy ID

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        operatorAuthorityService.deleteAccountOperatorAuthority(userId, accountId);

        verify(authorityRepository, times(1)).findByUserId(userId);
        verify(authorityDetailsRepository, times(1)).deleteById(authority.getId());
    }

    @Test
    public void testGetOperatorUserAuthorityDetails() {
        String userId = "testUserId";
        Long accountId = 123L;

        Authority authority = new Authority();
        authority.setId(1L);

        CcaAuthorityDetails authorityDetails = new CcaAuthorityDetails();

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(authority));
        when(authorityService.getAuthorityDetails(authority.getId())).thenReturn(authorityDetails);

        CcaAuthorityDetails result = operatorAuthorityService.getOperatorUserAuthorityDetails(userId, accountId);

        assertNotNull(result);
        assertEquals(authorityDetails, result);

        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(authorityService, times(1)).getAuthorityDetails(authority.getId());
    }

    @Test
    public void testGetOperatorUserAuthorityDetails_AuthorityNotFound() {
        String userId = "someUserId";
        Long accountId = 123L;

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> operatorAuthorityService.getOperatorUserAuthorityDetails(userId, accountId));

        assertEquals(AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, exception.getErrorCode());

        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(authorityDetailsRepository, never()).findById(anyLong());
    }
    @Test
    void deleteAccountOperatorAuthority() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_basic_user")
                .status(AuthorityStatus.ACTIVE)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        operatorAuthorityService.deleteAccountOperatorAuthority(userId, accountId);

        verify(authorityRepository, times(1)).findByUserId(userId);
        verify(authorityDetailsRepository, times(1)).deleteById(authority.getId());
        verify(eventPublisher, times(1)).publishEvent(CcaOperatorAuthorityDeletionEvent.builder()
                .userId(userId).accountId(accountId).existAuthoritiesOnOtherAccounts(false).build());
    }

    @Test
    void deleteAccountOperatorAuthority_user_not_related_to_account() {
        String userId = "userId";
        Long accountId = 1L;
        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(2L)
                .code("operator_basic_user")
                .status(AuthorityStatus.ACTIVE)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> operatorAuthorityService.deleteAccountOperatorAuthority(userId, accountId));

        assertThat(businessException.getErrorCode()).isEqualTo(AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT);
        verifyNoMoreInteractions(authorityDetailsRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deleteAccountOperatorAuthority_do_not_delete_user() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority1 = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_basic_user")
                .status(AuthorityStatus.ACCEPTED)
                .build();
        Authority authority2 = Authority.builder()
                .userId(userId)
                .accountId(2L)
                .code("operator_basic_user")
                .status(AuthorityStatus.ACCEPTED)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        operatorAuthorityService.deleteAccountOperatorAuthority(userId, accountId);

        verify(authorityDetailsRepository, times(1)).deleteById(authority1.getId());
        verify(eventPublisher, times(1)).publishEvent(CcaOperatorAuthorityDeletionEvent.builder()
                .userId(userId).accountId(accountId).existAuthoritiesOnOtherAccounts(true).build());
    }
}
