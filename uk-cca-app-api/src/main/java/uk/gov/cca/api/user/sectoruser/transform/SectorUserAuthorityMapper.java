package uk.gov.cca.api.user.sectoruser.transform;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = StringUtils.class)
public interface SectorUserAuthorityMapper {

    @Mapping(target = "contactType", source = "sectorUserAuthority.contactType.name")
    @Mapping(target = "roleCode", source = "sectorUserAuthority.roleCode")
    @Mapping(target = "roleName", source = "sectorUserAuthority.roleName")
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "userId", source = "userInfo.userId")
    @Mapping(target = "status", source = "sectorUserAuthority.authorityStatus")
    SectorUserAuthorityInfoDTO toSectorUsersAuthoritiesInfoDto(SectorUserAuthorityDTO sectorUserAuthority, UserInfoDTO userInfo);
}
