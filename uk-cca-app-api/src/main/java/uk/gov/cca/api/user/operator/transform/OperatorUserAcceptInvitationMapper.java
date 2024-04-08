package uk.gov.cca.api.user.operator.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.cca.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.cca.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.cca.api.user.operator.domain.OperatorUserDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface OperatorUserAcceptInvitationMapper {

     OperatorInvitedUserInfoDTO toOperatorInvitedUserInfoDTO(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation,
                                                             String roleCode, UserInvitationStatus invitationStatus);

     @Mapping(target = "userAuthorityId", source = "authorityInfoDTO.id")
     @Mapping(target = "userAuthenticationStatus", source = "operatorUserDTO.status")
     @Mapping(target = "userId", source = "authorityInfoDTO.userId")
     OperatorUserAcceptInvitationDTO toOperatorUserAcceptInvitationDTO(OperatorUserDTO operatorUserDTO,
                                                                       AuthorityInfoDTO authorityInfoDTO,
                                                                       String accountInstallationName);
}
