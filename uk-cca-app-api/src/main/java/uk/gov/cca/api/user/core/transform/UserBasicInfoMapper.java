package uk.gov.cca.api.user.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.user.core.domain.UserBasicInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserBasicInfoMapper {

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserBasicInfoDTO toUserBasicInfoDTO(UserInfoDTO userInfoDTO);
}
