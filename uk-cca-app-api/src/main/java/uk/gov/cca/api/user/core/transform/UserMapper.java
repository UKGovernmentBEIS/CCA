package uk.gov.cca.api.user.core.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.cca.api.user.core.domain.model.UserInfo;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.Optional;

/**
 * The User Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "email", source = "username")
    @Mapping(target = "userId", source = "id")
    UserInfoDTO toUserInfoDTO(UserRepresentation userRepresentation);

    @Mapping(target = "userId", source = "userInfo.id")
    @Mapping(target = "locked", ignore = true)
    UserInfoDTO toUserInfoDTONoLockedInfo(UserInfo userInfo);

    @Mapping(target = "userId", source = "userInfo.id")
    @Mapping(target = "locked", expression = "java(Boolean.valueOf(!userInfo.isEnabled()))")
    UserInfoDTO toUserInfoDTO(UserInfo userInfo);
    
    @AfterMapping
    default void populateAttributesToUserInfoDTO(UserRepresentation userRepresentation, @MappingTarget UserInfoDTO userInfoDTO) {
        if (!ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()))
                    .ifPresent(list -> userInfoDTO.setStatus(AuthenticationStatus.valueOf(list.get(0))));
        }
    }
}
