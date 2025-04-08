package uk.gov.cca.api.user.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.user.core.service.UserInfoService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorUserInfoServiceTest {

    @InjectMocks
    private SectorUserInfoService sectorUserInfoService;

    @Mock
    private UserInfoService userInfoService;


    @Test
    void getSectorUsersAuthoritiesInfoWithEditAuthority() {
        List<String> userIds = List.of("userId");

        List<UserInfoDTO> userInfoDTOList = List.of(UserInfoDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .build());

        when(userInfoService.getUsersInfo(userIds)).thenReturn(userInfoDTOList);

        List<UserInfoDTO> result = sectorUserInfoService.getSectorUsersInfo(userIds);
        assertEquals(userInfoDTOList, result);

        verify(userInfoService, times(1)).getUsersInfo(userIds);
    }
}
