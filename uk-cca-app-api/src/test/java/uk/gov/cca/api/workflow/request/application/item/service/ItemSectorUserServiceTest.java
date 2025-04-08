package uk.gov.cca.api.workflow.request.application.item.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemByRequestSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemRequestResourcesService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

class ItemSectorUserServiceTest {

    @Mock
    private SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemByRequestSectorUserRepository itemByRequestSectorUserRepository;
    
    @Mock
    private ItemRequestResourcesService itemRequestResourcesService;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private ItemSectorUserService itemSectorUserService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetItemsByRequest() {
        String requestId = "requestId";
        AppUser appUser = new AppUser();
        appUser.setUserId("userId");

        Request request = new Request();
        request.setRequestResources(List.of(RequestResource.builder()
        		.resourceType(CcaResourceType.SECTOR_ASSOCIATION)
        		.resourceId("1")
        		.build()));

        Map<Long, Set<String>> userScopedRequestTaskTypes = Collections.singletonMap(1L, Collections.singleton("requestTaskType1"));
        Map<String, Map<String, String>> expectedItemRequestResources = 
        		Map.of("requestId", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "sectorId"));

        ItemPage itemPage = ItemPage.builder().build();
        ItemDTOResponse expectedResponse = ItemDTOResponse.builder().build();

        when(requestService.findRequestById(anyString())).thenReturn(request);
        when(sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypesBySector(appUser, 1L))
                .thenReturn(userScopedRequestTaskTypes);
        when(itemRequestResourcesService.getItemRequestResources(itemPage)).thenReturn(expectedItemRequestResources);
        when(itemByRequestSectorUserRepository.findItemsByRequestId(userScopedRequestTaskTypes, requestId)).thenReturn(itemPage);
        when(itemResponseService.toItemDTOResponse(itemPage, expectedItemRequestResources, appUser)).thenReturn(expectedResponse);


        itemSectorUserService.getItemsByRequest(appUser, requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(sectorUserAuthorityResourceAdapter, times(1)).getUserScopedRequestTaskTypesBySector(appUser, 1L);
        verify(itemRequestResourcesService, times(1)).getItemRequestResources(itemPage);
        verify(itemByRequestSectorUserRepository, times(1)).findItemsByRequestId(userScopedRequestTaskTypes, requestId);
        verify(itemResponseService, times(1)).toItemDTOResponse(itemPage, expectedItemRequestResources, appUser);
    }
}