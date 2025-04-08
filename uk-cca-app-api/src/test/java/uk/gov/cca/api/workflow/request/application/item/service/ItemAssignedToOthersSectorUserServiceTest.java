package uk.gov.cca.api.workflow.request.application.item.service;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
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

import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
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
class ItemAssignedToOthersSectorUserServiceTest {

	@InjectMocks
    private ItemAssignedToOthersSectorUserService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemSectorUserRepository itemRepository;
    
    @Mock
    private ItemRequestResourcesService itemRequestResourcesService;

    @Mock
    private SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;
    
    @Test
    void getItemsAssignedToOthers() {
        final String userId = "userId";
        final Long sectorId = 1L;
        final AppUser appUser = buildSectorUser(userId, "username", sectorId);
        Map<Long, Set<String>> scopedRequestTaskTypes =
                Map.of(sectorId, Set.of("requestTaskType1"));
        Map<String, Map<String, String>> expectedItemRequestResources = 
        		Map.of("requestId", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "sectorId"));

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder().items(List.of(expectedItem)).totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder().items(List.of(expectedItemDTO)).totalItems(1L).build();

        // Mock
        when(sectorUserAuthorityResourceAdapter
            .getUserScopedRequestTaskTypes(appUser))
            .thenReturn(scopedRequestTaskTypes);
        when(itemRequestResourcesService.getItemRequestResources(expectedItemPage)).thenReturn(expectedItemRequestResources);
        doReturn(expectedItemPage).when(itemRepository).findItems(
        		appUser.getUserId(), ItemAssignmentType.OTHERS, scopedRequestTaskTypes, PagingRequest.builder()
	        		.pageNumber(0L)
	        		.pageSize(10L)
	        		.build());
        doReturn(expectedItemDTOResponse).when(itemResponseService)
        	.toItemDTOResponse(expectedItemPage, expectedItemRequestResources, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(sectorUserAuthorityResourceAdapter, times(1))
                .getUserScopedRequestTaskTypes(appUser);
        verify(itemRepository, times(1)).findItems(
        		appUser.getUserId(), ItemAssignmentType.OTHERS, scopedRequestTaskTypes, PagingRequest.builder()
	        		.pageNumber(0L)
	        		.pageSize(10L)
	        		.build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, expectedItemRequestResources, appUser);
    }

    @Test
    void getItemsAssignedToOthers_empty_scopes() {
    	final String userId = "userId";
        final Long sectorId = 1L;
        final AppUser appUser = buildSectorUser(userId, "username", sectorId);
        Map<Long, Set<String>> scopedRequestTaskTypes = emptyMap();

        // Mock
        when(sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser))
        .thenReturn(scopedRequestTaskTypes);

		// Invoke
		ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
		
		// Assert
		assertThat(actualItemDTOResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());
		
		verify(sectorUserAuthorityResourceAdapter, times(1))
		    .getUserScopedRequestTaskTypes(appUser);
		verify(itemRepository, never()).findItems(anyString(), Mockito.any(), anyMap(), any(PagingRequest.class));
		verify(itemResponseService, never()).toItemDTOResponse(any(), any(), any());
    }

    @Test
    void getRoleType() {
        assertEquals(SECTOR_USER, itemService.getRoleType());
    }

    private AppUser buildSectorUser(String userId, String username, Long sectorId) {
    	AppCcaAuthority appAuthority = AppCcaAuthority.builder()
                .sectorAssociationId(sectorId)
                .build();
    	
        return AppUser.builder()
                .userId(userId)
                .firstName(username)
                .lastName(username)
                .authorities(List.of(appAuthority))
                .roleType(SECTOR_USER)
                .build();
    }
}
