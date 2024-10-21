package uk.gov.cca.api.authorization.ccaauth.operator.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.AccountAuthorizationResourceService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;

@ExtendWith(MockitoExtension.class)
class CcaOperatorAuthorityQueryServiceTest {

    @InjectMocks
    private CcaOperatorAuthorityQueryService operatorAuthorityQueryService;
    @Mock
    private AccountAuthorizationResourceService accountAuthorizationResourceService;

    @Mock
    private CcaAuthorityRepository ccaAuthorityRepository;

    @Test
    void getOperatorAuthoritiesTest() {
        Long accountId = 1L;
        String roleCode = "operator_basic_user";
        String roleName = "Operator";
        String userId = "userId";
        AppCcaAuthority appAuthority = AppCcaAuthority.builder()
                .accountId(accountId)
                .permissions(List.of(CcaPermission.PERM_USER_READONLY))
                .build();
        AppUser appUser = AppUser.builder()
                .userId(userId)
                .roleType(OPERATOR)
                .authorities(List.of(appAuthority)).build();

        final OperatorAuthorityDTO operatorAuthorityDTO_1 = OperatorAuthorityDTO.builder()
        		.userId("user_1")
        		.roleName(roleName)
        		.roleCode(roleCode)
        		.authorityStatus(AuthorityStatus.PENDING)
        		.authorityCreationDate(null)
        		.contactType(ContactType.OPERATOR)
        		.build();

        final OperatorAuthorityDTO operatorAuthorityDTO_2 = OperatorAuthorityDTO.builder()
        		.userId("user_2")
        		.roleName(roleName)
        		.roleCode(roleCode)
        		.authorityStatus(AuthorityStatus.ACTIVE)
        		.authorityCreationDate(null)
        		.contactType(ContactType.OPERATOR)
        		.build();

        when(accountAuthorizationResourceService.hasUserScopeToAccount(appUser, accountId, Scope.EDIT_USER)).thenReturn(false);
        when(ccaAuthorityRepository.findAuthoritiesWithDetailsByAccountId(accountId)).thenReturn(List.of(operatorAuthorityDTO_1, operatorAuthorityDTO_2));

        //invoke
        final OperatorAuthoritiesDTO operatorAuthorities = operatorAuthorityQueryService.getOperatorAuthorities(appUser, accountId);

        //assert
        assertThat(operatorAuthorities.getAuthorities().size()).isEqualTo(2);
        assertThat(operatorAuthorities.isEditable()).isEqualTo(false);

        verify(accountAuthorizationResourceService, times(1)).hasUserScopeToAccount(appUser, accountId, Scope.EDIT_USER);
        verify(ccaAuthorityRepository, times(1)).findAuthoritiesWithDetailsByAccountId(accountId);
    }


}
