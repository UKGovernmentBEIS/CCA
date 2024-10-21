package uk.gov.cca.api.user.operator.transform;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.cca.api.user.operator.domain.OperatorAuthorityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = StringUtils.class)
public interface OperatorAuthorityMapper {

    @Mapping(target = "contactType", source = "operatorAuthority.contactType.name")
    @Mapping(target = "roleCode", source = "operatorAuthority.roleCode")
    @Mapping(target = "roleName", source = "operatorAuthority.roleName")
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "userId", source = "userInfo.userId")
    @Mapping(target = "status", source = "operatorAuthority.authorityStatus")
    OperatorAuthorityInfoDTO toOperatorAuthorityInfoDto(OperatorAuthorityDTO operatorAuthority, UserInfoDTO userInfo);
}
