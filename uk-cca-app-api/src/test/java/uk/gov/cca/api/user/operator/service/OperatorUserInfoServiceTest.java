package uk.gov.cca.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.cca.api.authorization.rules.services.resource.AccountAuthorizationResourceService;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.service.UserInfoService;
import uk.gov.cca.api.user.operator.service.OperatorUserInfoService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserInfoServiceTest {
    @InjectMocks
    private OperatorUserInfoService operatorUserInfoService;

    @Mock
    private AccountAuthorizationResourceService accountAuthorizationResourceService;

    @Mock
    private UserInfoService userInfoService;

    @Test
    void getOperatorUsersInfo() {
        AppUser authUser = new AppUser();
        Long accountId = 1L;
        List<UserInfoDTO> userInfoDTOList = List.of(UserInfoDTO.builder()
                .locked(true)
                .firstName("firstName")
                .lastName("lastName")
                .build());
        boolean hasUserScopeToAccount = true;

        when(accountAuthorizationResourceService.hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER)).thenReturn(hasUserScopeToAccount);
        when(userInfoService.getUsersInfo(List.of("userId"), hasUserScopeToAccount)).thenReturn(userInfoDTOList);

        List<UserInfoDTO> result = operatorUserInfoService.getOperatorUsersInfo(authUser, accountId, List.of("userId"));
        assertEquals(userInfoDTOList, result);
    }
}