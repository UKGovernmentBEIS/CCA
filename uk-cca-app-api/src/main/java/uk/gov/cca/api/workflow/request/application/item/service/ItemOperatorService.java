package uk.gov.cca.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemByRequestOperatorRepository;
import uk.gov.cca.api.workflow.request.core.domain.Request;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemOperatorService implements ItemService {

    private final OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemByRequestOperatorRepository itemByRequestOperatorRepository;
    private final RequestService requestService;

    @Override
    public ItemDTOResponse getItemsByRequest(AppUser appUser, String requestId) {
        Request request = requestService.findRequestById(requestId);
        Long accountId = request.getAccountId();
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = operatorAuthorityResourceAdapter
                .getUserScopedRequestTaskTypesByAccountId(appUser.getUserId(), accountId);

        if (ObjectUtils.isEmpty(userScopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemByRequestOperatorRepository.findItemsByRequestId(userScopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, appUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }
}
