package uk.gov.cca.api.targetperiodreporting.performancedata.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountPerformanceDataStatusMapper {

	@Mapping(target = "targetPeriodName", source = "entity.targetPeriod.name")
	@Mapping(target = "targetPeriodType", source = "entity.targetPeriod.businessId")
	@Mapping(target = "reportVersion", source = "entity.lastPerformanceData.reportVersion")
	AccountPerformanceDataStatusInfoDTO toAccountPerformanceDataStatusInfoDTO(AccountPerformanceDataStatus entity);

}
