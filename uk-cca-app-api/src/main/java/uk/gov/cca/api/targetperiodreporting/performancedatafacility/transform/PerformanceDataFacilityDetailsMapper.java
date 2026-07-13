package uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataFacilityDetailsMapper {

	@Mapping(target = "atLeastSeventyPercentEnergyUsed", source = "data.energyFuelDetails.atLeastSeventyPercentEnergyUsed")
	FacilityPerformanceDataReportDetailsDTO toFacilityPerformanceDataReportDetailsDTO(PerformanceDataFacilityContainer data, TargetPeriodType targetPeriod);

}
