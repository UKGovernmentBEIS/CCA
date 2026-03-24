package uk.gov.cca.api.facility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.FacilityValidationContext;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityHeaderInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {FacilityDataStatus.class})
public interface FacilityDetailsMapper {

    @Mapping(target = "status", expression = "java(facilityData.getClosedDate() == null ? FacilityDataStatus.LIVE : FacilityDataStatus.INACTIVE)")
    FacilityDataDetailsDTO toFacilityDetailsResult(FacilityData facilityData);

    FacilityDTO toFacilityDTO(FacilityData facilityData);

    FacilityBaseInfoDTO toFacilityBaseInfo(FacilityData facilityData);

    FacilityValidationContext toFacilityValidationContext(FacilityData facilityData);

    @Mapping(target = "name", source = "siteName")
    @Mapping(target = "businessId", source = "facilityBusinessId")
    @Mapping(target = "status", expression = "java(facilityData.getClosedDate() == null ? FacilityDataStatus.LIVE : FacilityDataStatus.INACTIVE)")
    FacilityHeaderInfoDTO toFacilityHeaderInfo(FacilityData facilityData);
}
