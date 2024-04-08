package uk.gov.cca.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToMeOperatorService;
import uk.gov.cca.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.domain.Item;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemOperatorRepository;
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

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeOperatorServiceTest {

    @InjectMocks
    private ItemAssignedToMeOperatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemOperatorRepository itemOperatorRepository;

    @Mock
    private OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToMe_task_assignee_true() {
        final Long accountId = 1L;
        AppUser appUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(accountId, Set.of());

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L)
                .build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L)
                .build();

        // Mock
        doReturn(scopedRequestTaskTypes)
            .when(operatorAuthorityResourceAdapter).getUserScopedRequestTaskTypes(appUser);
        doReturn(expectedItemPage).when(itemOperatorRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME,
            scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(operatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypes(appUser);
        verify(itemOperatorRepository, times(1)).findItems(appUser.getUserId(), ItemAssignmentType.ME,
            scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
    }

    @Test
    void getItemsAssignedToMe_task_assignee_false() {
        final Long accountId = 1L;
        AppUser appUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(accountId, Set.of());

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L)
                .build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L)
                .build();

        // Mock
        doReturn(scopedRequestTaskTypes)
            .when(operatorAuthorityResourceAdapter).getUserScopedRequestTaskTypes(appUser);
        doReturn(expectedItemPage).when(itemOperatorRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME,
            scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(operatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypes(appUser);
        verify(itemOperatorRepository, times(1)).findItems(appUser.getUserId(), ItemAssignmentType.ME,
            scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
    }

    @Test
    void getItemsAssignedToMe_no_user_authorities() {
        final Long accountId = 1L;
        AppUser appUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypesAsString = emptyMap();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of())
                .totalItems(0L).build();

        // Mock
        doReturn(scopedRequestTaskTypesAsString)
            .when(operatorAuthorityResourceAdapter).getUserScopedRequestTaskTypes(appUser);
        doReturn(expectedItemPage).when(itemOperatorRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypesAsString, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
            .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verify(operatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypes(appUser);
        verify(itemOperatorRepository, times(1))
                .findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypesAsString, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.OPERATOR, itemService.getRoleType());
    }

    private AppUser buildOperatorUser(String userId, String firstName, String lastName, Long accountId) {
        AppAuthority appAuthority = AppAuthority.builder()
                .accountId(accountId)
                .build();

        return AppUser.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .authorities(List.of(appAuthority))
                .roleType(RoleType.OPERATOR)
                .build();
    }
}
