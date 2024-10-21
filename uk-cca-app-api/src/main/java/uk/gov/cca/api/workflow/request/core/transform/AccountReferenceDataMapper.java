package uk.gov.cca.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.transform.AccountAddressMapper;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", uses = {AccountAddressMapper.class}, config = MapperConfig.class)
public interface AccountReferenceDataMapper {

    default AccountReferenceData toAccountReferenceData(TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO, SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO){
        return AccountReferenceData.builder()
                .targetUnitAccountDetails(toTargetUnitAccountDetails(targetUnitAccountDetailsDTO))
                .sectorAssociationDetails(toSectorAssociationDetails(sectorAssociationMeasurementInfoDTO))
                .build();
    }

    @Mapping(target = "operatorName", source = "name")
    TargetUnitAccountDetails toTargetUnitAccountDetails(TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO);

    @Mapping(target = "measurementType", expression =  "java(uk.gov.cca.api.common.domain.MeasurementType.getMeasurementTypeByUnit(sectorAssociationMeasurementInfoDTO.getMeasurementUnit()))")
    SectorAssociationDetails toSectorAssociationDetails(SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO);
}
