package uk.gov.cca.api.user.core.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.model.UserInfo;
import uk.gov.cca.api.user.core.transform.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserInfoDTONoLockedInfo() {
        String userId = "userId";
        String fName = "fName";
        String lName = "lName";
        UserInfo userInfo = UserInfo.builder().id(userId).firstName(fName).lastName(lName).enabled(true).build();

        UserInfoDTO expectDTO = UserInfoDTO.builder()
            .userId(userId)
            .firstName(fName)
            .lastName(lName)
            .build();

        UserInfoDTO actualDTO = userMapper.toUserInfoDTONoLockedInfo(userInfo);

        assertEquals(expectDTO, actualDTO);
    }

    @Test
    void toUserInfoDTO() {
        String userId = "userId";
        String fName = "fName";
        String lName = "lName";
        UserInfo userInfo = UserInfo.builder().id(userId).firstName(fName).lastName(lName).enabled(true).build();

        UserInfoDTO expectDTO = UserInfoDTO.builder()
            .userId(userId)
            .firstName(fName)
            .lastName(lName)
            .locked(false)
            .build();

        UserInfoDTO actualDTO = userMapper.toUserInfoDTO(userInfo);

        assertEquals(expectDTO, actualDTO);
    }
}