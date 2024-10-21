package uk.gov.cca.api.authorization.ccaauth.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AppCcaUserMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "roleType", source = "roleType")
    AppUser toAppUser(String userId, String email, String firstName, String lastName, List<CcaAuthorityDTO> authorities, String roleType);

    @Mapping(target = "permissions", source = "authorityPermissions")
    AppCcaAuthority toAppCcaAuthority(CcaAuthorityDTO authority);
}
