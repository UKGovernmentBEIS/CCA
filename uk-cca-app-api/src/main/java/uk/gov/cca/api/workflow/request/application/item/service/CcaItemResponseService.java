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
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.transform.TargetUnitItemMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
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
    private final UserAuthService userAuthService;
    private final UserRoleTypeService userRoleTypeService;
    private static final TargetUnitItemMapper targetUnitItemMapper = Mappers.getMapper(TargetUnitItemMapper.class);
    
	@Override
    public ItemDTOResponse toItemDTOResponse(ItemPage itemPage, AppUser appUser) {
        //get user info from keycloak for the task assignee ids
        Map<String, UserInfoDTO> users = getUserInfoForItemAssignees(appUser, itemPage);

        Map<Long, ItemTargetUnitAccountDTO> accounts = getAccounts(itemPage);

        List<ItemDTO> itemDTOs = itemPage.getItems().stream().map(item -> {
            String taskAssigneeId = item.getTaskAssigneeId();
            UserInfoDTO taskAssigneeInfo = taskAssigneeId != null ? users.get(taskAssigneeId) : null;
            String taskAssigneeType = taskAssigneeId != null ? userRoleTypeService.getUserRoleTypeByUserId(taskAssigneeId).getRoleType() : null;
            ItemTargetUnitAccountDTO account = accounts.get(item.getAccountId());
            return targetUnitItemMapper.itemToItemTargetUnitDTO(item,
                taskAssigneeInfo,
                taskAssigneeType,
                account);
        }).collect(Collectors.toList());

        return ItemDTOResponse.builder()
            .items(itemDTOs)
            .totalItems(itemPage.getTotalItems())
            .build();
    }

    private Map<Long, ItemTargetUnitAccountDTO> getAccounts(ItemPage itemPage) {
        List<Long> accountIds = itemPage.getItems()
            .stream().map(Item::getAccountId)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(accountIds))
            return Collections.emptyMap();

        return targetUnitAccountQueryService.getAccountsByIds(accountIds).stream()
            .map(targetUnitItemMapper::accountToItemTargetUnitAccountDTO)
            .collect(Collectors.toMap(ItemTargetUnitAccountDTO::getAccountId, itemDTO -> itemDTO));
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
