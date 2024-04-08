package uk.gov.cca.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.model.UserInfo;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.core.transform.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserAuthService userAuthService;
    private static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public List<UserInfoDTO> getUsersInfo(List<String> userIds, boolean hasAuthorityToEditUsers) {
        Map<String, UserInfo> userInfo = userAuthService.getUsers(userIds).stream()
            .collect(Collectors.toMap(UserInfo::getId, user -> user));

        List<UserInfoDTO> userInfoList;

        if (hasAuthorityToEditUsers) {
            userInfoList = userInfo.values().stream()
                .map(userMapper::toUserInfoDTO)
                .collect(Collectors.toList());
        } else {
            userInfoList = userInfo.values().stream()
                .map(userMapper::toUserInfoDTONoLockedInfo)
                .collect(Collectors.toList());
        }
        return userInfoList;
    }
}
