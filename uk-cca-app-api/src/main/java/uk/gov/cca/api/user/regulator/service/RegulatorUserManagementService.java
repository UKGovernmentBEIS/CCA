package uk.gov.cca.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.cca.api.user.core.service.UserSecuritySetupService;
import uk.gov.cca.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.cca.api.user.regulator.domain.RegulatorUserUpdateDTO;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Service
@RequiredArgsConstructor
public class RegulatorUserManagementService {

    private final RegulatorUserAuthService regulatorUserAuthService;

    private final RegulatorAuthorityService regulatorAuthorityService;
    
    private final UserSecuritySetupService userSecuritySetupService;

    public RegulatorUserDTO getRegulatorUserByUserId(AppUser user, String userId) {
        // Validate
        validateRegulatorUser(user, userId);

        return regulatorUserAuthService.getRegulatorUserById(userId);
    }

    @Transactional
    public void updateRegulatorUserByUserId(AppUser appUser, String userId, RegulatorUserDTO regulatorUserUpdateDTO, FileDTO signature) {
        validateRegulatorUser(appUser, userId);

        regulatorUserAuthService.updateRegulatorUser(userId, regulatorUserUpdateDTO, signature);
    }

    @Transactional
    public void updateCurrentRegulatorUser(AppUser appUser, RegulatorUserUpdateDTO regulatorUserUpdateDTO, FileDTO signature) {
        regulatorUserAuthService.updateRegulatorUser(appUser.getUserId(), regulatorUserUpdateDTO.getUser(), signature);
    }

    public void resetRegulator2Fa(AppUser appUser, String userId) {
        validateRegulatorUser(appUser, userId);
        userSecuritySetupService.resetUser2Fa(userId);
    }

    private void validateRegulatorUser(AppUser appUser, String userId) {
        if (!regulatorAuthorityService.existsByUserIdAndCompetentAuthority(userId, appUser.getCompetentAuthority())) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA);
        }
    }
}
