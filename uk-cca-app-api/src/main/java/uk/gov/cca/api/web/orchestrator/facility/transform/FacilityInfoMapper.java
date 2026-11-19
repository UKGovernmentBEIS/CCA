package uk.gov.cca.api.web.orchestrator.facility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResultExtendedDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityInfoMapper {

    FacilitySearchResultExtendedDTO toFacilityCertificationSearchResultInfo(FacilitySearchResultInfoDTO facilitySearchResultInfo, FacilityCertificationStatus certificationStatus);

    @Mapping(target = "facilityId", source = "facilityDetails.id")
    FacilityInfoDTO toFacilityInfoDTO(FacilityDataDetailsDTO facilityDetails, SurplusGainedDTO surplusDetails);

}
