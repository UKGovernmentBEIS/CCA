package uk.gov.cca.api.workflow.request.application.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemRequestResourcesService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemResponseService;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedSectorUserServiceTest {

	@InjectMocks
    private ItemUnassignedSectorUserService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemSectorUserRepository itemRepository;
    
    @Mock
    private ItemRequestResourcesService itemRequestResourcesService;

    @Mock
    private SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;
    
    @Test
    void getUnassignedItems() {
        Map<Long, Set<String>> scopedRequestTaskTypes = Map.of(1L, Set.of("requestTaskType1"));
        Map<String, Map<String, String>> expectedItemRequestResources = 
        		Map.of("requestId", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "sectorId"));

        AppUser appUser = AppUser.builder().userId("userId").roleType(SECTOR_USER).build();
        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRequestResourcesService.getItemRequestResources(expectedItemPage)).thenReturn(expectedItemRequestResources);
        when(itemRepository.findItems(appUser.getUserId(), ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build()))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, expectedItemRequestResources, appUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = itemService.getUnassignedItems(appUser, PagingRequest.builder().pageNumber(0).pageSize(10).build());

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);

        verify(sectorUserAuthorityResourceAdapter, times(1)).getUserScopedRequestTaskTypes(appUser);
        verify(itemRequestResourcesService, times(1)).getItemRequestResources(expectedItemPage);
    }

    @Test
    void getUnassignedItems_empty_scopes() {
        Map<Long, Set<String>> scopedRequestTaskTypes = Map.of();

        AppUser appUser = AppUser.builder().userId("userId").roleType(SECTOR_USER).build();

        // Mock
        when(sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser))
                .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = itemService.getUnassignedItems(appUser, PagingRequest.builder().pageNumber(0).pageSize(10).build());

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(sectorUserAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypes(appUser);
        verify(itemRepository, never()).findItems(anyString(), Mockito.any(), anyMap(), any(PagingRequest.class));
        verify(itemResponseService, never()).toItemDTOResponse(any(), any(), any());
    }

    @Test
    void getUnassignedItems_ReturnsEmptyResponseWhenNoItemsFetched() {
        Map<Long, Set<String>> scopedRequestTaskTypes = Map.of(1L, Set.of("requestTaskType1"));
        Map<String, Map<String, String>> expectedItemRequestResources = 
        		Map.of("requestId", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "sectorId"));

        AppUser appUser = AppUser.builder().userId("userId").roleType(SECTOR_USER).build();
        ItemPage itemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRequestResourcesService.getItemRequestResources(itemPage)).thenReturn(expectedItemRequestResources);
        when(itemRepository.findItems(appUser.getUserId(), ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build()))
                .thenReturn(itemPage);
        when(itemResponseService.toItemDTOResponse(itemPage, expectedItemRequestResources, appUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = itemService.getUnassignedItems(appUser, PagingRequest.builder().pageNumber(0).pageSize(10).build());

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);
    }

    @Test
    void getRoleType() {
        assertEquals(SECTOR_USER, itemService.getRoleType());
    }
}
