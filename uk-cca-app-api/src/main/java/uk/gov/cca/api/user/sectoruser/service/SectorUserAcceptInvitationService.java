package uk.gov.cca.api.user.sectoruser.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.user.sectoruser.domain.SectorInvitedUserInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserWithAuthorityDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserAcceptInvitationMapper;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;

@Service
@RequiredArgsConstructor
public class SectorUserAcceptInvitationService {
    private final SectorUserAuthService sectorUserAuthService;
    private final SectorUserTokenVerificationService sectorUserTokenVerificationService;
    private final SectorUserAcceptInvitationMapper sectorUserAcceptInvitationMapper;
    private final SectorUserRoleCodeAcceptInvitationService sectorUserRoleCodeAcceptInvitationService;
    private final CcaAuthorityDetailsRepository ccaAuthorityDetailsRepository;
    private final SectorUserRegisterValidationService sectorUserRegisterValidationService;

    @Transactional
    public SectorInvitedUserInfoDTO acceptInvitation(String invitationToken) {
        CcaAuthorityInfoDTO authority =
                sectorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        sectorUserRegisterValidationService.validateRegisterForSectorAssociation(authority.getUserId(), authority.getSectorAssociationId());

        CcaAuthorityDetails authorityDetails = ccaAuthorityDetailsRepository.findCcaAuthorityDetailsByAuthorityId(authority.getId());
        SectorUserDTO userDTO = sectorUserAuthService.getUserById(authority.getUserId());

        SectorUserWithAuthorityDTO sectorUserWithAuthorityDTO = sectorUserAcceptInvitationMapper
                .toSectorUserWithAuthorityDTO(userDTO, authority);

        UserInvitationStatus invitationStatus = sectorUserRoleCodeAcceptInvitationService
                .acceptInvitation(sectorUserWithAuthorityDTO, authority.getCode());

        return sectorUserAcceptInvitationMapper
                .toSectorInvitedUserInfoDTO(sectorUserWithAuthorityDTO, authority.getCode(),
                        invitationStatus, authorityDetails.getContactType());
    }
}
