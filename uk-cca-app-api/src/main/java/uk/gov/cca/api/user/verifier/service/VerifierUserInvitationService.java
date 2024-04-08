package uk.gov.cca.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.cca.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.cca.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.cca.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.user.verifier.transform.VerifierUserMapper;
import uk.gov.cca.api.verificationbody.service.VerificationBodyQueryService;

@Service
@Log4j2
@RequiredArgsConstructor
public class VerifierUserInvitationService {

    private final VerifierUserAuthService verifierUserAuthService;
    private final VerifierAuthorityService verifierAuthorityService;
    private final VerifierUserNotificationGateway verifierUserNotificationGateway;
    private final VerifierUserTokenVerificationService verifierUserTokenVerificationService;
    private final VerificationBodyQueryService verificationBodyQueryService;
    private final VerifierUserMapper verifierUserMapper = Mappers.getMapper(VerifierUserMapper.class);

    /**
     *  Invites a new verifier user to join verification body with a specified role.
     * @param appUser the current logged-in {@link AppUser}
     * @param verifierUserInvitation the {@link VerifierUserInvitationDTO}
     */
    @Transactional
    public void inviteVerifierUser(AppUser appUser, VerifierUserInvitationDTO verifierUserInvitation) {
        Long verificationBodyId = appUser.getVerificationBodyId();
        inviteVerifierUser(appUser, verifierUserInvitation, verificationBodyId);
    }

    /**
     * Invites a new verifier user to join verification body with VERIFIER ADMIN role.
     * @param appUser the current logged-in {@link AppUser}
     * @param adminVerifierUserInvitationDTO the {@link AdminVerifierUserInvitationDTO}
     * @param verificationBodyId the id of the verification body to which the user will join
     */
    @Transactional
    public void inviteVerifierAdminUser(AppUser appUser, AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO,
                                        Long verificationBodyId) {
        VerifierUserInvitationDTO verifierUserInvitationDTO =
            verifierUserMapper.toVerifierUserInvitationDTO(adminVerifierUserInvitationDTO);

        // Validate that non disabled verification body exists
        if (!verificationBodyQueryService.existsNonDisabledVerificationBodyById(verificationBodyId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
        }

        inviteVerifierUser(appUser, verifierUserInvitationDTO, verificationBodyId);
    }

    public InvitedUserInfoDTO acceptInvitation(String invitationToken) {
        AuthorityInfoDTO authorityInfo = verifierUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        VerifierUserDTO user = verifierUserAuthService.getVerifierUserById(authorityInfo.getUserId());

        if (user.getStatus() != AuthenticationStatus.PENDING) {
            log.error("User '{}' found with status '{}'", authorityInfo::getUserId, user::getStatus);
            throw new BusinessException(ErrorCode.USER_INVALID_STATUS);
        }

        return InvitedUserInfoDTO.builder().email(user.getEmail()).build();
    }

    private void inviteVerifierUser(AppUser appUser, VerifierUserInvitationDTO verifierUserInvitation,
                                    Long verificationBodyId) {
        String userId = verifierUserAuthService.registerInvitedVerifierUser(verifierUserInvitation);

        String authorityUuid = verifierAuthorityService.createPendingAuthority(verificationBodyId,
            verifierUserInvitation.getRoleCode(), userId, appUser);

        verifierUserNotificationGateway.notifyInvitedUser(verifierUserInvitation, authorityUuid);
    }
}
