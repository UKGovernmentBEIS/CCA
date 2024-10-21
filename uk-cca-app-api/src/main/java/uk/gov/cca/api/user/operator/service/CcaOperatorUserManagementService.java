package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityUpdateService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDetailsDTO;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserViewMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.service.auth.AuthService;

@Service
@RequiredArgsConstructor
public class CcaOperatorUserManagementService {

    private final CcaOperatorUserViewMapper ccaOperatorUserViewMapper;

    private final CcaOperatorAuthorityService ccaOperatorAuthorityService;
    
    private final CcaOperatorAuthorityUpdateService ccaOperatorAuthorityUpdateService;

    private final AuthService authService;

    private final CcaOperatorUserAuthService ccaOperatorUserAuthService;

    public CcaOperatorUserDetailsDTO getOperatorUserByAccountIdAndUserId(String userId, Long accountId) {
        CcaAuthorityDetails ccaAuthorityDetails = ccaOperatorAuthorityService.getOperatorUserAuthorityDetails(userId, accountId);
        return ccaOperatorUserViewMapper.toCcaOperatorUserDetailsDTO(authService.getUserRepresentationById(userId), ccaAuthorityDetails);
    }

    public void updateCurrentOperatorUser(AppUser appUser, Long accountId, CcaOperatorUserDetailsDTO updatedOperatorUserDetailsDTO) {

        final String userId = appUser.getUserId();

        // Validate and update CcaAuthorityDetails
        ccaOperatorAuthorityUpdateService.updateOperatorUserAuthorityDetails(userId, accountId, updatedOperatorUserDetailsDTO.getOrganisationName());

        // update operator user
        ccaOperatorUserAuthService.updateCcaOperatorUser(updatedOperatorUserDetailsDTO);
    }

    public void updateOperatorUserByAccountAndUserId(Long accountId, String userId, CcaOperatorUserDetailsDTO ccaOperatorUserDetailsDTO) {

    	ccaOperatorAuthorityUpdateService.updateOperatorUserAuthorityDetailsWithContactType(userId, accountId, ccaOperatorUserDetailsDTO.getOrganisationName(), ccaOperatorUserDetailsDTO.getContactType());

        // update sector user
        ccaOperatorUserAuthService.updateCcaOperatorUser(ccaOperatorUserDetailsDTO);
    }

}
