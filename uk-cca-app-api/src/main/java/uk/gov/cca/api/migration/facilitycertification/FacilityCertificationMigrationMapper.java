package uk.gov.cca.api.migration.facilitycertification;

import org.mapstruct.Mapper;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityCertificationMigrationMapper {

	FacilityCertificationDTO toFacilityCertificationDTO(FacilityCertificationVO vo);
}
