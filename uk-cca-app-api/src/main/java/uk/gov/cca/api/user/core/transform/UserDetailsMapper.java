package uk.gov.cca.api.user.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserDetailsMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "signature.content", source = "signature.fileContent")
    @Mapping(target = "signature.name", source = "signature.fileName")
    @Mapping(target = "signature.type", source = "signature.fileType")
    @Mapping(target = "signature.size", source = "signature.fileSize")
    UserDetailsRequest toUserDetails(String userId, FileDTO signature);
}
