package uk.gov.cca.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToMeVerifierService;
import uk.gov.cca.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.domain.Item;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemVerifierRepository;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeVerifierServiceTest {

    @InjectMocks
    private ItemAssignedToMeVerifierService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemVerifierRepository itemRepository;

    @Mock
    private VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToMe() {
        final String userId = "vb1Id";
        final Long vbId = 1L;
        final AppUser appUser = buildVerifierUser(userId, "vb1", vbId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(vbId, Set.of(mock(RequestTaskType.class)));

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder().items(List.of(expectedItem)).totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder().items(List.of(expectedItemDTO)).totalItems(1L).build();

        // Mock
        when(verifierAuthorityResourceAdapter
            .getUserScopedRequestTaskTypes(appUser))
            .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(verifierAuthorityResourceAdapter, times(1))
                .getUserScopedRequestTaskTypes(appUser);
        verify(itemRepository, times(1)).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
    }

    @Test
    void getItemsAssignedToMe_no_user_authorities() {
        final Long vbId = 1L;
        final AppUser appUser = buildVerifierUser("vb1Id", "vb1", vbId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = emptyMap();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of())
                .totalItems(0L).build();

        // Mock
        doReturn(scopedRequestTaskTypes)
            .when(verifierAuthorityResourceAdapter).getUserScopedRequestTaskTypes(appUser);
        doReturn(expectedItemPage).when(itemRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verify(verifierAuthorityResourceAdapter, times(1))
                .getUserScopedRequestTaskTypes(appUser);
        verify(itemRepository, times(1))
                .findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.VERIFIER, itemService.getRoleType());
    }

    private AppUser buildVerifierUser(String userId, String username, Long vbId) {
        return AppUser.builder()
                .userId(userId)
                .firstName(username)
                .lastName(username)
                .authorities(List.of(AppAuthority.builder().verificationBodyId(vbId).build()))
                .roleType(RoleType.VERIFIER)
                .build();
    }
}
