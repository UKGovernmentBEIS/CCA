package uk.gov.cca.api.workflow.request.application.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorUserAuthorityResourceService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityResourceAdapterTest {

	@InjectMocks
    private SectorUserAuthorityResourceAdapter sectorUserAuthorityResourceAdapter;

    @Mock
    private SectorUserAuthorityResourceService sectorUserAuthorityResourceService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;


    @Test
    void getUserScopedRequestTaskTypes() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId)
                .authorities(List.of(AppAuthority.builder()
                        .permissions(List.of()).build()))
                .build();
        final Long sectorId1 = 1L;
        final Long sectorId2 = 2L;
        final Set<Long> sectors = Set.of(sectorId1, sectorId2);

        String requestTaskType1 = "requestTaskType1";
        String requestTaskType2 = "requestTaskType2";

        when(sectorUserAuthorityResourceService.findUserScopedRequestTaskTypesBySectorAssociationIds(userId, Set.of(sectorId1, sectorId2)))
                .thenReturn(Map.of(sectorId1, Set.of(requestTaskType1, requestTaskType2)));
        when(sectorAssociationQueryService.getUserSectorAssociationIds(user)).thenReturn(sectors);
        
        Map<Long, Set<String>> userScopedRequestTaskTypes =
        		sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypes(user);

        assertThat(userScopedRequestTaskTypes).containsExactlyInAnyOrderEntriesOf(
                Map.of(sectorId1, Set.of(requestTaskType1, requestTaskType2))
        );
    }
    
    @Test
    void getUserScopedRequestTaskTypesBySector() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId)
                .authorities(List.of(AppAuthority.builder()
                        .permissions(List.of()).build()))
                .build();
        final Long sectorId1 = 1L;

        String requestTaskType1 = "requestTaskType1";
        String requestTaskType2 = "requestTaskType2";

        when(sectorUserAuthorityResourceService.findUserScopedRequestTaskTypesBySectorAssociationIds(userId, Set.of(sectorId1)))
                .thenReturn(Map.of(sectorId1, Set.of(requestTaskType1, requestTaskType2)));
        Map<Long, Set<String>> userScopedRequestTaskTypes =
                sectorUserAuthorityResourceAdapter.getUserScopedRequestTaskTypesBySector(user, sectorId1);

        assertThat(userScopedRequestTaskTypes).containsExactlyInAnyOrderEntriesOf(
                Map.of(sectorId1, Set.of(requestTaskType1, requestTaskType2))
        );
    }
}
