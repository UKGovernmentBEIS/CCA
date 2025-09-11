package uk.gov.cca.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountReferenceDataMapper {

    @Mapping(target = "operatorName", source = "name")
    TargetUnitAccountDetails toTargetUnitAccountDetails(TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO);
}
