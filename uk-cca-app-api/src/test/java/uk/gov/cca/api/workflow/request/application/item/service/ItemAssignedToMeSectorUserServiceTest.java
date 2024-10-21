package uk.gov.cca.api.workflow.request.application.item.service;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeSectorUserServiceTest {

	@InjectMocks
    private ItemAssignedToMeSectorUserService itemService;

    @Mock
    private CcaItemResponseService itemResponseService;

    @Mock
    private ItemSectorUserRepository itemRepository;

    @Mock
    private SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;
    
    @Test
    void getItemsAssignedToMe() {
        final String userId = "userId";
        final Long sectorId = 1L;
        final AppUser appUser = buildSectorUser(userId, "username", sectorId);
        Map<Long, Set<String>> scopedRequestTaskTypes =
                Map.of(sectorId, Set.of("requestTaskType1"));

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder().items(List.of(expectedItem)).totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder().items(List.of(expectedItemDTO)).totalItems(1L).build();

        // Mock
        when(sectorUserAuthorityResourceAdapter
            .getUserScopedRequestTaskTypes(appUser))
            .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(sectorUserAuthorityResourceAdapter, times(1))
                .getUserScopedRequestTaskTypes(appUser);
        verify(itemRepository, times(1)).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
    }

    @Test
    void getItemsAssignedToMe_no_user_authorities() {
    	final String userId = "userId";
        final Long sectorId = 1L;
        final AppUser appUser = buildSectorUser(userId, "username", sectorId);
        Map<Long, Set<String>> scopedRequestTaskTypes = emptyMap();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of())
                .totalItems(0L).build();

        // Mock
        doReturn(scopedRequestTaskTypes)
            .when(sectorUserAuthorityResourceAdapter).getUserScopedRequestTaskTypes(appUser);
        doReturn(expectedItemPage).when(itemRepository).findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verify(sectorUserAuthorityResourceAdapter, times(1))
                .getUserScopedRequestTaskTypes(appUser);
        verify(itemRepository, times(1))
                .findItems(appUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, appUser);
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
