package uk.gov.cca.api.web.orchestrator.authorization.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.cca.api.web.orchestrator.authorization.dto.RegulatorUserAuthorityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.user.regulator.domain.RegulatorUserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorUserAuthorityInfoMapper {

    @Mapping(target = "locked", expression = "java(userInfo.getEnabled()!=null ? !userInfo.getEnabled() : null)")
    RegulatorUserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, RegulatorUserInfoDTO userInfo);
}