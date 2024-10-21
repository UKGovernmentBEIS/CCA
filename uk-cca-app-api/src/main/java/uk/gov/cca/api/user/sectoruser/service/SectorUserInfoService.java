package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.user.core.service.UserInfoService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorUserInfoService {

    private final UserInfoService userInfoService;

    public List<UserInfoDTO> getSectorUsersInfo(List<String> userIds) {
        return userInfoService.getUsersInfo(userIds);
    }

}
