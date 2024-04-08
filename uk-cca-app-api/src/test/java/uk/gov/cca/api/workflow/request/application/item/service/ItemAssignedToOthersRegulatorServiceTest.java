package uk.gov.cca.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToOthersRegulatorService;
import uk.gov.cca.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.domain.Item;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemRegulatorRepository;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToOthersRegulatorServiceTest {

    @InjectMocks
    private ItemAssignedToOthersRegulatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemRegulatorRepository itemRegulatorRepository;
    
    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToOthers() {
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(mock(RequestTaskType.class)));
        
        AppUser appUser = buildRegulatorUser("reg1Id");
        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRegulatorRepository).findItems(appUser.getUserId(), ItemAssignmentType.OTHERS,
                scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, appUser);


        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        
        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypes(appUser.getUserId());
    }
    
    @Test
    void getItemsAssignedToOthers_empty_scopes() {
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        
        AppUser appUser = buildRegulatorUser("reg1Id");

        // Mock
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertThat(actualItemDTOResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());
        
        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypes(appUser.getUserId());
        verify(itemRegulatorRepository, never()).findItems(Mockito.any(), Mockito.any() , Mockito.any(), Mockito.any());
        verify(itemResponseService, never()).toItemDTOResponse(Mockito.any(), Mockito.any());
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.REGULATOR, itemService.getRoleType());
    }

    private AppUser buildRegulatorUser(String userId) {
        return AppUser.builder()
                .userId(userId)
                .roleType(RoleType.REGULATOR)
                .build();
    }
}
