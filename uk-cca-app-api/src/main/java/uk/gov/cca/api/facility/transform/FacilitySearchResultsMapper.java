package uk.gov.cca.api.facility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {FacilityDataStatus.class})
public interface FacilitySearchResultsMapper {

    @Mapping(target = "id", source = "facilityId")
    @Mapping(target = "status", expression = "java(facility.getClosedDate() == null ? FacilityDataStatus.LIVE : FacilityDataStatus.INACTIVE)")
    FacilitySearchResultInfoDTO toFacilitySearchResultInfo(FacilityData facility);

}
