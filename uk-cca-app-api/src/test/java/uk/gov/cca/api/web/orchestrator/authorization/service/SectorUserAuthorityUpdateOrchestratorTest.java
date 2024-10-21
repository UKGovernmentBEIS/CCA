package uk.gov.cca.api.web.orchestrator.authorization.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityUpdateService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityUpdateWrapperDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserNotificationGateway;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityUpdateOrchestratorTest {

	@InjectMocks
    private SectorUserAuthorityUpdateOrchestrator service;

    @Mock
    private SectorUserAuthorityUpdateService sectorUserAuthorityUpdateService;

    @Mock
    private SectorUserNotificationGateway sectorUserNotificationGateway;

    @Test
    void updateSectorUserAuthorities() {
        Long sectorId = 1L;

        List<SectorUserAuthorityUpdateDTO> sectorUsers = List.of(
                SectorUserAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACCEPTED).build(),
                SectorUserAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        SectorUserAuthorityUpdateWrapperDTO wrapper = new SectorUserAuthorityUpdateWrapperDTO(sectorUsers);

        List<NewUserActivated> activatedSectorUsers = List.of(NewUserActivated.builder().userId("1").build());

        when(sectorUserAuthorityUpdateService.updateSectorUserAuthorities(wrapper.getSectorUserAuthorityUpdateDTOList(), sectorId))
                .thenReturn(activatedSectorUsers);

        service.updateSectorAuthorities(wrapper, sectorId);

        verify(sectorUserNotificationGateway, times(1)).notifyUsersUpdateStatus(activatedSectorUsers);
        verify(sectorUserAuthorityUpdateService, times(1))
                .updateSectorUserAuthorities(wrapper.getSectorUserAuthorityUpdateDTOList(), sectorId);
    }
    
    @Test
    void updateSectorUserAuthorities_empty_notifications() {
    	Long sectorId = 1L;

        List<SectorUserAuthorityUpdateDTO> sectorUsers = List.of(
                SectorUserAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACCEPTED).build(),
                SectorUserAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        SectorUserAuthorityUpdateWrapperDTO wrapper = new SectorUserAuthorityUpdateWrapperDTO(sectorUsers);

        when(sectorUserAuthorityUpdateService.updateSectorUserAuthorities(wrapper.getSectorUserAuthorityUpdateDTOList(), sectorId))
                .thenReturn(List.of());

        service.updateSectorAuthorities(wrapper, sectorId);

        verify(sectorUserAuthorityUpdateService, times(1))
                .updateSectorUserAuthorities(wrapper.getSectorUserAuthorityUpdateDTOList(), sectorId);
        verify(sectorUserNotificationGateway, never()).notifyUsersUpdateStatus(anyList());
    }
}
