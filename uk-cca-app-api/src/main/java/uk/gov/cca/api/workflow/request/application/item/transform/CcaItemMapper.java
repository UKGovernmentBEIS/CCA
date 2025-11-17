package uk.gov.cca.api.workflow.request.application.item.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.CcaItemDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemFacilityDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemSectorDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.core.domain.dto.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaItemMapper {

	@Mapping(target = "accountId", source = "id")
    @Mapping(target = "accountName", source = "name")
    ItemTargetUnitAccountDTO accountToItemTargetUnitAccountDTO(TargetUnitAccountDTO accountDTO);
	
	@Mapping(target = "sectorId", source = "id")
	@Mapping(target = "sectorAcronym", source = "acronym")
    @Mapping(target = "sectorName", source = "commonName")
	ItemSectorDTO sectorToItemSectorDTO(SectorAssociationDetailsDTO sectorDTO);
	
	@Mapping(target = "facilityId", source = "id")
    ItemFacilityDTO facilityToItemFacilityDTO(FacilityBaseInfoDTO facilityDTO);

	@Mapping(target = "requestType", source = "item.requestType.code")
	@Mapping(target = "taskType", source = "item.taskType.code")
    @Mapping(target = "itemAssignee.taskAssignee", source = "taskAssignee")
    @Mapping(target = "itemAssignee.taskAssigneeType", source = "taskAssigneeType")
    @Mapping(target = "daysRemaining", expression = "java(uk.gov.netz.api.common.utils.DateUtils.getDaysRemaining(item.getPauseDate(), item.getTaskDueDate()))")
    CcaItemDTO itemToCcaItemDTO(
    		Item item, UserInfoDTO taskAssignee, String taskAssigneeType, ItemTargetUnitAccountDTO account, ItemSectorDTO sector, ItemFacilityDTO facility);
}

