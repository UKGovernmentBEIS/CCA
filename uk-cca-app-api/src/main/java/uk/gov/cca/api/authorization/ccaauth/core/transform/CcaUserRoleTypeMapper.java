package uk.gov.cca.api.authorization.ccaauth.core.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaUserRoleType;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaUserRoleTypeMapper {

    UserRoleTypeDTO toUserRoleTypeDTO(CcaUserRoleType userRoleType);
}
