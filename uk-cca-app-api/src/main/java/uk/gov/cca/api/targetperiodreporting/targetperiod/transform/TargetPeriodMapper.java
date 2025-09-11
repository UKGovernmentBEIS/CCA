package uk.gov.cca.api.targetperiodreporting.targetperiod.transform;

import org.mapstruct.Mapper;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TargetPeriodMapper {

  TargetPeriodDTO toTargetPeriodDTO(TargetPeriod entity);

  TargetPeriod toTargetPeriod(TargetPeriodDTO dto);
}

