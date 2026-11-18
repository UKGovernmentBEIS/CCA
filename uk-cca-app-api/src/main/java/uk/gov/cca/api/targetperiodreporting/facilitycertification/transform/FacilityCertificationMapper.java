package uk.gov.cca.api.targetperiodreporting.facilitycertification.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityCertificationMapper {

    FacilityCertificationDTO toFacilityCertificationDto(FacilityCertification facilityCertification);

    FacilityCertification toFacilityCertification(FacilityCertificationDTO facilityCertificationDto);

	FacilityCertification toFacilityCertification(Long facilityId, FacilityCertificationStatusUpdateDTO dto);
}
