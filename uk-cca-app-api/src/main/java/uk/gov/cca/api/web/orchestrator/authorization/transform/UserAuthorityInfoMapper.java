package uk.gov.cca.api.web.orchestrator.authorization.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.cca.api.web.orchestrator.authorization.dto.UserAuthorityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserAuthorityInfoMapper {

    @Mapping(target = "userId", source = "userInfo.userId")
    @Mapping(target = "authorityCreationDate", source = "userAuthority.authorityCreationDate")
    @Mapping(target = "locked", expression = "java(userInfo.getLocked())")
    UserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, UserInfoDTO userInfo);
}
