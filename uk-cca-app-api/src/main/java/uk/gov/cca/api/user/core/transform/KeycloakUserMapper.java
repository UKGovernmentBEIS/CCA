package uk.gov.cca.api.user.core.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.user.core.domain.model.UserInfo;
import uk.gov.cca.api.user.core.domain.model.keycloak.KeycloakUserInfo;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface KeycloakUserMapper {

    UserInfo toUserInfo(KeycloakUserInfo keycloakUserInfo);
}
