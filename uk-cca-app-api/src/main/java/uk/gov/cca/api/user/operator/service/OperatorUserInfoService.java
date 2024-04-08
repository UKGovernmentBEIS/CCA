package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.cca.api.authorization.rules.services.resource.AccountAuthorizationResourceService;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.service.UserInfoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorUserInfoService {

    private final AccountAuthorizationResourceService accountAuthorizationResourceService;
    private final UserInfoService userInfoService;

    public List<UserInfoDTO> getOperatorUsersInfo(AppUser authUser, Long accountId, List<String> userIds) {
        boolean hasAuthUserEditUserScopeOnAccount =
            accountAuthorizationResourceService.hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER);

        return userInfoService.getUsersInfo(userIds, hasAuthUserEditUserScopeOnAccount);
    }
}
