package uk.gov.cca.api.workflow.request.application.item.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemSectorDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.transform.CcaItemMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.workflow.request.core.domain.dto.UserInfoDTO;

@Service
@RequiredArgsConstructor
public class CcaItemResponseService implements ItemResponseService {

	private final TargetUnitAccountQueryService targetUnitAccountQueryService;
	private final SectorAssociationQueryService sectorAssociationQueryService;
    private final UserAuthService userAuthService;
    private final UserRoleTypeService userRoleTypeService;
    private static final CcaItemMapper ccaItemMapper = Mappers.getMapper(CcaItemMapper.class);
    
	@Override
    public ItemDTOResponse toItemDTOResponse(
    		ItemPage itemPage, Map<String, Map<String, String>> itemRequestResources, AppUser appUser) {
        // Get user info from keycloak for the task assignee ids
        Map<String, UserInfoDTO> users = getUserInfoForItemAssignees(appUser, itemPage);

        // Get account information
        Map<String, ItemTargetUnitAccountDTO> accounts = getAccounts(itemRequestResources);
        // Get sector information
        Map<String, ItemSectorDTO> sectors = getSectors(itemRequestResources);

        List<ItemDTO> itemDTOs = itemPage.getItems().stream().map(item -> {
            String taskAssigneeId = item.getTaskAssigneeId();
            UserInfoDTO taskAssigneeInfo = taskAssigneeId != null ? users.get(taskAssigneeId) : null;
            String taskAssigneeType = taskAssigneeId != null ? userRoleTypeService.getUserRoleTypeByUserId(taskAssigneeId).getRoleType() : null;
            ItemTargetUnitAccountDTO account = accounts.get(itemRequestResources.get(item.getRequestId()).get(ResourceType.ACCOUNT));
            ItemSectorDTO sector = sectors.get(itemRequestResources.get(item.getRequestId()).get(CcaResourceType.SECTOR_ASSOCIATION));
            return ccaItemMapper.itemToCcaItemDTO(item,
                taskAssigneeInfo,
                taskAssigneeType,
                account,
                sector);
        }).collect(Collectors.toList());

        return ItemDTOResponse.builder()
            .items(itemDTOs)
            .totalItems(itemPage.getTotalItems())
            .build();
    }

	private Map<String, ItemTargetUnitAccountDTO> getAccounts(Map<String, Map<String, String>> itemRequestResources) {
        List<Long> accountIds = itemRequestResources.values()
            .stream()
            .map(resource -> resource.get(ResourceType.ACCOUNT))
            .filter(Objects::nonNull)
            .map(Long::parseLong)
            .toList();

        if (CollectionUtils.isEmpty(accountIds))
            return Collections.emptyMap();

        return targetUnitAccountQueryService.getAccountsByIds(accountIds).stream()
            .map(ccaItemMapper::accountToItemTargetUnitAccountDTO)
            .collect(Collectors.toMap(itemDTO -> itemDTO.getAccountId().toString(), itemDTO -> itemDTO));
    }
	
	private Map<String, ItemSectorDTO> getSectors(Map<String, Map<String, String>> itemRequestResources) {
		List<Long> sectorIds = itemRequestResources.values()
	            .stream()
	            .map(resource -> resource.get(CcaResourceType.SECTOR_ASSOCIATION))
	            .filter(Objects::nonNull)
	            .map(Long::parseLong)
	            .toList();

	        if (CollectionUtils.isEmpty(sectorIds))
	            return Collections.emptyMap();

	        return sectorAssociationQueryService.getSectorsByIds(sectorIds).stream()
	            .map(ccaItemMapper::sectorToItemSectorDTO)
	            .collect(Collectors.toMap(item -> item.getSectorId().toString(), itemDTO -> itemDTO));
	}

    private Map<String, UserInfoDTO> getUserInfoForItemAssignees(AppUser appUser, ItemPage itemPage) {
        Set<String> userIds = itemPage.getItems().stream()
            .map(Item::getTaskAssigneeId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(userIds))
            return Collections.emptyMap();

        //if the assignee of all items is the appUser
        if (userIds.size() == 1 && userIds.contains(appUser.getUserId()))
            return Map.of(appUser.getUserId(),
                new UserInfoDTO(appUser.getFirstName(), appUser.getLastName()));

        return userAuthService.getUsers(new ArrayList<>(userIds)).stream()
            .collect(Collectors.toMap(
                UserInfo::getId,
                u -> new UserInfoDTO(u.getFirstName(), u.getLastName())));
    }
}
