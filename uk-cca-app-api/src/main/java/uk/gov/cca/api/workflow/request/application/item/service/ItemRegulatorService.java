package uk.gov.cca.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemByRequestRegulatorRepository;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRegulatorService implements ItemService {

    private final RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemByRequestRegulatorRepository itemByRequestRegulatorRepository;

    @Override
    public ItemDTOResponse getItemsByRequest(AppUser appUser, String requestId) {
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
                .getUserScopedRequestTaskTypes(appUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemByRequestRegulatorRepository.findItemsByRequestId(scopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, appUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }
}
