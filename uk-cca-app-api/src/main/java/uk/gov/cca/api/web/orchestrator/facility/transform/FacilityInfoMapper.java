package uk.gov.cca.api.web.orchestrator.facility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationDetailsDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResultExtendedDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityInfoMapper {

    FacilitySearchResultExtendedDTO toFacilityCertificationSearchResultInfo(FacilitySearchResultInfoDTO facilitySearchResultInfo, FacilityCertificationStatus certificationStatus);

    @Mapping(target = "status", source = "facilityCertification.certificationStatus")
    @Mapping(target = "startDate", source = "facilityCertification.startDate")
    @Mapping(target = "certificationPeriod", source = "certificationPeriod.certificationPeriodType")
    @Mapping(target = "certificationPeriodStartDate", source = "certificationPeriod.startDate")
    @Mapping(target = "certificationPeriodEndDate", source = "certificationPeriod.endDate")
    FacilityCertificationDetailsDTO toFacilityCertificationDetails(FacilityCertificationDTO facilityCertification, CertificationPeriodDTO certificationPeriod);

    @Mapping(target = "facilityId", source = "facilityDetails.id")
    FacilityInfoDTO toFacilityInfoDTO(FacilityDataDetailsDTO facilityDetails, List<FacilityCertificationDetailsDTO> facilityCertificationDetails);

}
