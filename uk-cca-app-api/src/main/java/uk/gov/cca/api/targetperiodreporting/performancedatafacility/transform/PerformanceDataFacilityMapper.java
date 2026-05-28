package uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataFacilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetPeriod", source = "targetPeriod")
    PerformanceDataFacilityEntity toPerformanceDataFacilityEntity(PerformanceDataFacility performanceDataFacility, TargetPeriod targetPeriod, int reportVersion);
}
