package uk.gov.cca.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemVerifierRepository;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemUnassignedVerifierService implements ItemUnassignedService {

    private final ItemVerifierRepository itemVerifierRepository;
    private final ItemResponseService itemResponseService;
    private final VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getUnassignedItems(AppUser appUser, PagingRequest paging) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser);

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemVerifierRepository.findItems(
                appUser.getUserId(),
                ItemAssignmentType.UNASSIGNED,
                scopedRequestTaskTypes,
                paging);

        return itemResponseService.toItemDTOResponse(itemPage, appUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
