package uk.gov.cca.api.user.operator.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.operator.domain.CcaOperatorInvitedUserInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.netz.api.user.operator.domain.OperatorUserWithAuthorityDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaOperatorUserAcceptInvitationMapper {

    CcaOperatorInvitedUserInfoDTO toOperatorInvitedUserInfoDTO(OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO,
                                                               String roleCode,
                                                               UserInvitationStatus invitationStatus,
                                                               ContactType contactType);
}
