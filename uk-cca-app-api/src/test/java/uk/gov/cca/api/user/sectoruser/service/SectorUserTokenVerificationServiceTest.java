package uk.gov.cca.api.user.sectoruser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.token.CcaJwtTokenAction;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.token.JwtTokenService;

@ExtendWith(MockitoExtension.class)
class SectorUserTokenVerificationServiceTest {

    @InjectMocks
    private SectorUserTokenVerificationService sectorUserTokenVerificationService;

    @Mock
    private JwtTokenService jwtTokenService;
    
    @Mock
    private CcaAuthorityService authorityService;

    @Test
    void verifyInvitationToken() {
    	String userId = "user";
    	AppUser user = AppUser.builder().userId(userId).build();
    	String invitationToken = "invitationToken";
        JwtTokenAction jwtTokenAction = CcaJwtTokenAction.SECTOR_USER_INVITATION;
        String authorityUuid = "authorityUuid";
        CcaAuthorityInfoDTO authorityInfo = CcaAuthorityInfoDTO.builder()
            .id(1L)
            .userId(userId)
            .authorityStatus(AuthorityStatus.PENDING)
            .sectorAssociationId(1L)
            .build();

        when(jwtTokenService.resolveTokenActionClaim(invitationToken, jwtTokenAction)).thenReturn(authorityUuid);
        when(authorityService.findCcaAuthorityByUuidAndStatusPending(authorityUuid)).thenReturn(Optional.of(authorityInfo));

        CcaAuthorityInfoDTO result = sectorUserTokenVerificationService.verifyInvitationToken(invitationToken, user);

        assertThat(result).isEqualTo(authorityInfo);

        verify(jwtTokenService, times(1)).resolveTokenActionClaim(invitationToken, jwtTokenAction);
        verify(authorityService, times(1)).findCcaAuthorityByUuidAndStatusPending(authorityUuid);
    }
}
