package uk.gov.cca.api.user.operator.service;

import uk.gov.cca.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.cca.api.user.operator.domain.OperatorUserAcceptInvitationDTO;

import java.util.Set;

public interface OperatorRoleCodeAcceptInvitationService {

    UserInvitationStatus acceptInvitation(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation);

    Set<String> getRoleCodes();
}
