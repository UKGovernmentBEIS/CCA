package uk.gov.cca.api.workflow.request.application.item.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.workflow.request.application.authorization.SectorUserAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemByRequestSectorUserRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemResponseService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

public class ItemSectorUserServiceTest {

    @Mock
    private SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemByRequestSectorUserRepository itemByRequestSectorUserRepository;

    @Mock
    private RequestService requestService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @InjectMocks
    private ItemSectorUserService itemSectorUserService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetItemsByRequest() {
        String requestId = "requestId";
        AppUser appUser = new AppUser();
        appUser.setUserId("userId");

        Request request = new Request();
        request.setAccountId(1L);

        Map<Long, Set<String>> userScopedRequestTaskTypes = Collections.singletonMap(1L, Collections.singleton("requestTaskType1"));

        ItemPage itemPage = ItemPage.builder().build();
        ItemDTOResponse expectedResponse = ItemDTOResponse.builder().build();

        when(requestService.findRequestById(anyString())).thenReturn(request);
        when(sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypesBySector(any(AppUser.class), anyLong()))
                .thenReturn(userScopedRequestTaskTypes);
        when(itemByRequestSectorUserRepository.findItemsByRequestId(anyMap(), anyString())).thenReturn(itemPage);
        when(itemResponseService.toItemDTOResponse(any(ItemPage.class), any(AppUser.class))).thenReturn(expectedResponse);
        when(targetUnitAccountQueryService.getAccountById(anyLong()))
                .thenReturn(TargetUnitAccount.builder().id(1L).sectorAssociationId(2L).build());

        itemSectorUserService.getItemsByRequest(appUser, requestId);

        verify(requestService, times(1)).findRequestById(anyString());
        verify(sectorUserAuthorityResourceAdapter, times(1)).getUserScopedRequestTaskTypesBySector(any(AppUser.class), anyLong());
        verify(itemByRequestSectorUserRepository, times(1)).findItemsByRequestId(anyMap(), anyString());
        verify(itemResponseService, times(1)).toItemDTOResponse(any(ItemPage.class), any(AppUser.class));
    }
}