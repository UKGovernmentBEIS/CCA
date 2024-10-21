package uk.gov.cca.api.authorization.ccaauth.core.transform;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityWithPermissionDTO;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaAuthorityMapper {

    @Mapping(source = "permissions", target = "authorityPermissions", qualifiedByName = "toList")
    CcaAuthorityDTO toCcaAuthorityDTO(CcaAuthorityWithPermissionDTO authority);

    @Named("toList")
    default List<String> toList(String permissions) {
        return !StringUtils.isEmpty(permissions) ? Arrays.asList(permissions.split(",")) : new ArrayList<>();
    }
    
    @Mapping(source = "status", target = "authorityStatus")
    CcaAuthorityInfoDTO toCcaAuthorityInfoDTO(CcaAuthority authority);
}
