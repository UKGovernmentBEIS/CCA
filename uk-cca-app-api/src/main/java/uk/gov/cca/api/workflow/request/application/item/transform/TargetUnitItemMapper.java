package uk.gov.cca.api.workflow.request.application.item.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.core.domain.dto.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TargetUnitItemMapper {

	@Mapping(target = "accountId", source = "id")
    @Mapping(target = "accountName", source = "name")
    ItemTargetUnitAccountDTO accountToItemTargetUnitAccountDTO(TargetUnitAccountDTO accountDTO);

	@Mapping(target = "requestType", source = "item.requestType.code")
	@Mapping(target = "taskType", source = "item.taskType.code")
    @Mapping(target = "itemAssignee.taskAssignee", source = "taskAssignee")
    @Mapping(target = "itemAssignee.taskAssigneeType", source = "taskAssigneeType")
    @Mapping(target = "daysRemaining", expression = "java(uk.gov.netz.api.workflow.utils.DateUtils.getDaysRemaining(item.getPauseDate(), item.getTaskDueDate()))")
    ItemTargetUnitDTO itemToItemTargetUnitDTO(Item item, UserInfoDTO taskAssignee, String taskAssigneeType, ItemTargetUnitAccountDTO account);
}

