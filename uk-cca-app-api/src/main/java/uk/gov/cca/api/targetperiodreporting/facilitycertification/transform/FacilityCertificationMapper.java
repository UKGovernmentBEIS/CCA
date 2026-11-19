package uk.gov.cca.api.targetperiodreporting.facilitycertification.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityCertificationMapper {

    FacilityCertificationDTO toFacilityCertificationDto(FacilityCertification facilityCertification);

    FacilityCertification toFacilityCertification(FacilityCertificationDTO facilityCertificationDto);

	FacilityCertification toFacilityCertification(Long facilityId, FacilityCertificationStatusUpdateDTO dto);

    @Mapping(target = "certificationPeriodId", source = "certificationPeriod.id")
    @Mapping(target = "status", source = "facilityCertification.certificationStatus")
    @Mapping(target = "startDate", source = "facilityCertification.startDate")
    @Mapping(target = "certificationPeriod", source = "certificationPeriod.certificationPeriodType")
    @Mapping(target = "certificationPeriodStartDate", source = "certificationPeriod.startDate")
    @Mapping(target = "certificationPeriodEndDate", source = "certificationPeriod.endDate")
    FacilityCertificationStatusDTO toFacilityCertificationStatusDTO(FacilityCertificationDTO facilityCertification, CertificationPeriodDTO certificationPeriod);
}
