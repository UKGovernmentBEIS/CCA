package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;

@Service
@RequiredArgsConstructor
public class SectorUserActivationService {

    private final SectorUserAuthService sectorUserAuthService;
    private final CcaAuthorityService authorityService;
    private final SectorUserTokenVerificationService sectorUserTokenVerificationService;
    private final SectorUserRegisteredAcceptInvitationService sectorUserRegisteredAcceptInvitationService;
    private final SectorUserRegisterValidationService sectorUserRegisterValidationService;

    public SectorUserDTO acceptAuthorityAndEnableInvitedUserWithCredentials(SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO, AppUser appUser) {
    	CcaAuthorityInfoDTO authority = sectorUserTokenVerificationService
            .verifyInvitationToken(sectorUserRegistrationWithCredentialsDTO.getEmailToken(), appUser);

        if (sectorUserRegistrationWithCredentialsDTO.getOrganisationName() != null) {
        	authorityService.updateCcaAuthorityDetailsOrganisationName(authority.getId(), sectorUserRegistrationWithCredentialsDTO.getOrganisationName());
        }

        sectorUserRegisterValidationService.validateRegisterForSectorAssociation(authority.getUserId(), authority.getSectorAssociationId());

        SectorUserDTO sectorUserDTO = sectorUserAuthService
            .enableAndUpdateUserAndSetPassword(sectorUserRegistrationWithCredentialsDTO, authority.getUserId());

        sectorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authority.getId());

        return sectorUserDTO;
    }

    public void acceptAuthorityAndSetCredentialsToUser(InvitedUserCredentialsDTO invitedUserCredentialsDTO, AppUser appUser) {
        CcaAuthorityInfoDTO ccaAuthorityInfoDTO = sectorUserTokenVerificationService
                .verifyInvitationToken(invitedUserCredentialsDTO.getInvitationToken(), appUser);
        sectorUserRegisterValidationService.validateRegisterForSectorAssociation(ccaAuthorityInfoDTO.getUserId(), ccaAuthorityInfoDTO.getSectorAssociationId());
        sectorUserAuthService.setUserPassword(ccaAuthorityInfoDTO.getUserId(), invitedUserCredentialsDTO.getPassword());
        sectorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(ccaAuthorityInfoDTO.getId());
    }
}
