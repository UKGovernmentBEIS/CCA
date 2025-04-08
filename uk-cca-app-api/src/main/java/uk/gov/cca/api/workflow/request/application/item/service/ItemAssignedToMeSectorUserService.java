package uk.gov.cca.api.workflow.request.application.item.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToMeService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemRequestResourcesService;
import uk.gov.netz.api.common.domain.PagingRequest;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class ItemAssignedToMeSectorUserService implements ItemAssignedToMeService {

	private final ItemSectorUserRepository itemSectorUserRepository;
    private final CcaItemResponseService itemResponseService;
    private final ItemRequestResourcesService itemRequestResourcesService;
    private final SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getItemsAssignedToMe(AppUser appUser, PagingRequest paging) {
        Map<Long, Set<String>> userScopedRequestTaskTypes = sectorUserAuthorityResourceAdapter
                .getUserScopedRequestTaskTypes(appUser);

        ItemPage itemPage = itemSectorUserRepository.findItems(
                appUser.getUserId(),
                ItemAssignmentType.ME,
                userScopedRequestTaskTypes,
                paging);
        
        Map<String, Map<String, String>> itemRequestResources = 
        		itemRequestResourcesService.getItemRequestResources(itemPage);

        return itemResponseService.toItemDTOResponse(itemPage, itemRequestResources, appUser);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }
}
