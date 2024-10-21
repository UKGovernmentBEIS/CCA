package uk.gov.cca.api.authorization.ccaauth.core.service;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

class AppUserServiceTest {

    private final AppUserService appUserService = new AppUserService();

    @Test
    void getUserSectorAssociations() {
        AppCcaAuthority authority1 = AppCcaAuthority.builder().sectorAssociationId(1L).build();
        AppCcaAuthority authority2 = AppCcaAuthority.builder().sectorAssociationId(2L).build();
        AppUser appUser = AppUser.builder()
            .roleType(SECTOR_USER)
            .authorities(List.of(authority1, authority2))
            .build();

        Set<Long> result = appUserService.getUserSectorAssociations(appUser);

        assertThat(result).containsExactlyInAnyOrder(1L, 2L);
    }
}