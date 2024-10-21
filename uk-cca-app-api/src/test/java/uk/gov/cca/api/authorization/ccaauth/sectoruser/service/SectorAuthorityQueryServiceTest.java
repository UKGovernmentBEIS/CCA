package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class SectorAuthorityQueryServiceTest {

    @InjectMocks
    private SectorAuthorityQueryService sectorAuthorityQueryService;

    @Mock
    private SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Mock
    private CcaAuthorityRepository ccaAuthorityRepository;

    @Test
    void getSectorUserAuthorities() {
        Long sectorId = 1L;
        boolean isEditable = true;
        AppUser appUser = AppUser.builder().userId("current_user_id").build();
        String editSectorUser = Scope.EDIT_USER;
        SectorUserAuthorityDTO sectorUserAuthorityDTO = SectorUserAuthorityDTO.builder()
        		.userId("b87a35cf-e85e-483c-9c3d-0de3fccb283f")
        		.roleName("Administrator User")
        		.roleCode("roleCode")
        		.authorityStatus(ACTIVE)
        		.authorityCreationDate(LocalDateTime.now())
        		.contactType(ContactType.SECTOR_ASSOCIATION)
        		.build();

        when(sectorAssociationAuthorizationResourceService.hasUserScopeToSectorAssociation(appUser, editSectorUser, sectorId)).thenReturn(isEditable);
        when(ccaAuthorityRepository.findAuthoritiesWithDetailsBySectorAssociationId(sectorId)).thenReturn(Collections.singletonList(sectorUserAuthorityDTO));

        SectorUserAuthoritiesDTO sectorUserAuthorities = sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId);

        assertThat(sectorUserAuthorities.getAuthorities().get(0)).isEqualTo(sectorUserAuthorityDTO);

        verify(sectorAssociationAuthorizationResourceService, times(1)).hasUserScopeToSectorAssociation(appUser, editSectorUser, sectorId);
        verify(ccaAuthorityRepository, times(1)).findAuthoritiesWithDetailsBySectorAssociationId(sectorId);
    }
}
