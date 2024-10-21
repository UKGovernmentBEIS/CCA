package uk.gov.cca.api.authorization.ccaauth.rules.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorUserAccountAuthorizationServiceTest {

    @InjectMocks
    private SectorUserAccountAuthorizationService service;
    @Mock
    private TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;
    @Mock
    private SectorUserSectorAssociationAuthService sectorUserSectorAssociationAuthService;

    @Test
    void isAuthorizedTest() {
        Long accountId = 1L;
        Long sectorAssociationId = 1L;
        String userId = "user_id";

        final AppCcaAuthority appCcaAuthority = AppCcaAuthority.builder().sectorAssociationId(sectorAssociationId).build();
        AppUser appUser = AppUser.builder().userId(userId).roleType(SECTOR_USER).authorities(List.of(appCcaAuthority)).build();

        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorAssociationId);
        when(sectorUserSectorAssociationAuthService.isAuthorized(appUser, sectorAssociationId)).thenReturn(true);

        //invoke
        final boolean authorized = service.isAuthorized(appUser, accountId);

        //assertions
        assertThat(authorized).isTrue();
    }
}
