package uk.gov.cca.api.workflow.request.application.item.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToOthersService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.common.domain.PagingRequest;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class ItemAssignedToOthersSectorUserService implements ItemAssignedToOthersService {

	private final ItemSectorUserRepository itemSectorUserRepository;
    private final ItemResponseService itemResponseService;
    private final SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getItemsAssignedToOthers(AppUser appUser, PagingRequest paging) {
        Map<Long, Set<String>> scopedRequestTaskTypes =
        		sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser);

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemSectorUserRepository.findItems(
                appUser.getUserId(),
                ItemAssignmentType.OTHERS,
                scopedRequestTaskTypes,
                paging);

        return itemResponseService.toItemDTOResponse(itemPage, appUser);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }
}
