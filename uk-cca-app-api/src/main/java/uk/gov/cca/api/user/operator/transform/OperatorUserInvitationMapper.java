package uk.gov.cca.api.user.operator.transform;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.user.operator.domain.OperatorUserInvitationDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatorUserInvitationMapper {

    @Mapping(target = "roleCode", source = "ccaOperatorUserInvitationDTO.roleCode")
    OperatorUserInvitationDTO toUserInvitationDTO(CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO);
}
