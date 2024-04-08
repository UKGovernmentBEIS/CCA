package uk.gov.cca.api.workflow.request.core.assignment.taskassign.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.user.core.domain.model.UserInfo;

/**
 * The AssigneeUserInfo Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AssigneeUserInfoMapper {

    AssigneeUserInfoDTO toAssigneeUserInfoDTO(UserInfo userInfo);
}
