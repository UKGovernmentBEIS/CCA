package uk.gov.cca.api.user.sectoruser.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.user.sectoruser.domain.SectorInvitedUserInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserWithAuthorityDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserAcceptInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;

@ExtendWith(MockitoExtension.class)
public class SectorUserAcceptInvitationServiceTest {

    @InjectMocks
    private SectorUserAcceptInvitationService sectorUserAcceptInvitationService;

    @Mock
    private SectorUserAuthService sectorUserAuthService;

    @Mock
    private SectorUserTokenVerificationService sectorUserTokenVerificationService;

    @Mock
    private CcaAuthorityDetailsRepository ccaAuthorityDetailsRepository;

    @Mock
    private SectorUserAcceptInvitationMapper sectorUserAcceptInvitationMapper;

    @Mock
    private SectorUserRoleCodeAcceptInvitationService sectorUserRoleCodeAcceptInvitationService;
    
    @Mock
    private SectorUserRegisterValidationService sectorUserRegisterValidationService;

    @Test
    void acceptInvitation() {
        String invitationToken = "token";
        String userId = "userId";
        Long sectorAssociationId = 1L;
        String authorityRoleCode = "roleCode";
        CcaAuthorityInfoDTO authorityInfo = CcaAuthorityInfoDTO.builder().userId(userId).sectorAssociationId(sectorAssociationId).code(authorityRoleCode).build();
        SectorUserDTO sectorUser = SectorUserDTO.builder().build();
        SectorUserWithAuthorityDTO sectorUserWithAuthorityDTO = SectorUserWithAuthorityDTO.builder().build();
        SectorInvitedUserInfoDTO sectorInvitedUserInfoDTO = SectorInvitedUserInfoDTO.builder().build();
        UserInvitationStatus userInvitationStatus = UserInvitationStatus.ACCEPTED;
        CcaAuthority authority = CcaAuthority.builder().userId(userId).status(AuthorityStatus.ACTIVE).build();
        CcaAuthorityDetails ccaAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(authority)
                .contactType(ContactType.CONSULTANT)
                .build();

        when(sectorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(ccaAuthorityDetailsRepository.findCcaAuthorityDetailsByAuthorityId(authorityInfo.getId())).thenReturn(ccaAuthorityDetails);
        when(sectorUserAuthService.getUserById(authorityInfo.getUserId())).thenReturn(sectorUser);
        when(sectorUserAcceptInvitationMapper.toSectorUserWithAuthorityDTO(sectorUser, authorityInfo))
                .thenReturn(sectorUserWithAuthorityDTO);
        when(sectorUserRoleCodeAcceptInvitationService.acceptInvitation(sectorUserWithAuthorityDTO, authorityInfo.getCode()))
                .thenReturn(userInvitationStatus);
        when(sectorUserAcceptInvitationMapper.toSectorInvitedUserInfoDTO(sectorUserWithAuthorityDTO, authorityRoleCode, userInvitationStatus, ContactType.CONSULTANT))
                .thenReturn(sectorInvitedUserInfoDTO);
        sectorUserAcceptInvitationService.acceptInvitation(invitationToken);

        verify(ccaAuthorityDetailsRepository, times(1)).findCcaAuthorityDetailsByAuthorityId(authorityInfo.getId());
        verify(sectorUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(sectorUserAuthService, times(1)).getUserById(userId);
        verify(sectorUserAcceptInvitationMapper, times(1)).
                toSectorUserWithAuthorityDTO(sectorUser, authorityInfo);
        verify(sectorUserRoleCodeAcceptInvitationService, times(1))
                .acceptInvitation(sectorUserWithAuthorityDTO, authorityRoleCode);
        verify(sectorUserAcceptInvitationMapper, times(1))
                .toSectorInvitedUserInfoDTO(sectorUserWithAuthorityDTO, authorityRoleCode, userInvitationStatus, ContactType.CONSULTANT);
        verify(sectorUserRegisterValidationService, times(1))
                .validateRegisterForSectorAssociation(authorityInfo.getUserId(), authorityInfo.getSectorAssociationId());
    }
}
