package uk.gov.cca.api.user.operator.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDetailsDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserMapper;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserRegistrationMapper;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserViewMapper;
import uk.gov.netz.api.user.core.service.auth.AuthService;

@Service
@RequiredArgsConstructor
public class CcaOperatorUserAuthService {

	private final AuthService authService;
    private final CcaOperatorUserViewMapper ccaOperatorUserViewMapper;
    private final CcaOperatorUserRegistrationMapper ccaOperatorUserRegistrationMapper;
    private final CcaOperatorUserMapper ccaOperatorUserMapper;

    public void updateCcaOperatorUser(CcaOperatorUserDetailsDTO updatedOperatorUserDTO) {
        UserRepresentation updatedUser = ccaOperatorUserViewMapper.toUserRepresentation(updatedOperatorUserDTO);
        authService.saveUser(updatedUser);
    }

    public CcaOperatorUserDTO enableAndUpdateUserAndSetPassword
            (CcaOperatorUserRegistrationWithCredentialsDTO ccaOperatorUserRegistrationWithCredentialsDTO, String userId) {

        UserRepresentation keycloakUser = authService.getUserRepresentationById(userId);
        UserRepresentation userRepresentation = ccaOperatorUserRegistrationMapper
                .toUserRepresentation(ccaOperatorUserRegistrationWithCredentialsDTO, keycloakUser.getEmail(), keycloakUser.getId());

        authService.enableAndSaveUserAndSetPassword(userRepresentation, ccaOperatorUserRegistrationWithCredentialsDTO.getPassword());

        return ccaOperatorUserMapper.toCcaOperatorUserDTO(userRepresentation);
    }

    public String registerOperatorUser(CcaOperatorUserInvitationDTO operatorUserInvitationDTO) {
        UserRepresentation userRepresentation = ccaOperatorUserMapper.toUserRepresentation(operatorUserInvitationDTO.getEmail(),
                operatorUserInvitationDTO.getFirstName(), operatorUserInvitationDTO.getLastName());
        return authService.saveUser(userRepresentation);
    }

    public CcaOperatorUserDTO setUserPassword(String userId, String password) {
        UserRepresentation userRepresentation = authService.setUserPassword(userId, password);
        return ccaOperatorUserMapper.toCcaOperatorUserDTO(userRepresentation);
    }
}
