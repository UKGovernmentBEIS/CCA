package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;

@Service
@RequiredArgsConstructor
public class SectorUserActivationService {

    private final SectorUserAuthService sectorUserAuthService;
    private final CcaAuthorityService authorityService;
    private final SectorUserTokenVerificationService sectorUserTokenVerificationService;
    private final SectorUserRegisteredAcceptInvitationService sectorUserRegisteredAcceptInvitationService;
    private final SectorUserRegisterValidationService sectorUserRegisterValidationService;

    public SectorUserDTO acceptAuthorityAndEnableInvitedUserWithCredentials(SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO) {
    	CcaAuthorityInfoDTO authority = sectorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(sectorUserRegistrationWithCredentialsDTO.getEmailToken());

        if (sectorUserRegistrationWithCredentialsDTO.getOrganisationName() != null) {
        	authorityService.updateCcaAuthorityDetailsOrganisationName(authority.getId(), sectorUserRegistrationWithCredentialsDTO.getOrganisationName());
        }

        sectorUserRegisterValidationService.validateRegisterForSectorAssociation(authority.getUserId(), authority.getSectorAssociationId());

        SectorUserDTO sectorUserDTO = sectorUserAuthService
            .enableAndUpdateUserAndSetPassword(sectorUserRegistrationWithCredentialsDTO, authority.getUserId());

        sectorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authority.getId());

        return sectorUserDTO;
    }

    public void acceptAuthorityAndSetCredentialsToUser(InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        CcaAuthorityInfoDTO ccaAuthorityInfoDTO = sectorUserTokenVerificationService
                .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        sectorUserRegisterValidationService.validateRegisterForSectorAssociation(ccaAuthorityInfoDTO.getUserId(), ccaAuthorityInfoDTO.getSectorAssociationId());
        sectorUserAuthService.setUserPassword(ccaAuthorityInfoDTO.getUserId(), invitedUserCredentialsDTO.getPassword());
        sectorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(ccaAuthorityInfoDTO.getId());
    }
}
