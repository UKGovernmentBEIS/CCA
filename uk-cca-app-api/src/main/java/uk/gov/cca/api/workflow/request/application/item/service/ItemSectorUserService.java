package uk.gov.cca.api.workflow.request.application.item.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemByRequestSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class ItemSectorUserService implements ItemService {

    private final SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemByRequestSectorUserRepository itemByRequestSectorUserRepository;
    private final RequestService requestService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;


    @Override
    public ItemDTOResponse getItemsByRequest(AppUser appUser, String requestId) {

        Request request = requestService.findRequestById(requestId);
        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(request.getAccountId());

        // :TODO: AccountID needs to be checked for future request implementation at Sector association level. [future-work]

        Map<Long, Set<String>> userScopedRequestTaskTypes = sectorUserAuthorityResourceAdapter
                .getUserScopedRequestTaskTypesBySector(appUser, sectorAssociationId);

        if (ObjectUtils.isEmpty(userScopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemByRequestSectorUserRepository.findItemsByRequestId(userScopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, appUser);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }
}