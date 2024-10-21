package uk.gov.cca.api.web.orchestrator.authorization.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.web.orchestrator.authorization.dto.RegulatorUserAuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.user.regulator.domain.RegulatorUserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorUserAuthorityInfoMapper {

    RegulatorUserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, RegulatorUserInfoDTO userInfo);
}