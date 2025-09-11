package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementAccountReferenceDataMapper {

    @Mapping(target = "responsiblePerson", source = "responsiblePersonDetails")
    TargetUnitAccountUpdateDTO toTargetUnitAccountUpdateDTO(UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails);
}
