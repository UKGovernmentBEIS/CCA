package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceAccountTemplateMapper {

	@Mapping(target = "targetPeriodType", source = "entity.targetPeriod.businessId")
	@Mapping(target = "targetPeriodName", source = "entity.targetPeriod.name")
	AccountPerformanceAccountTemplateDataReportInfoDTO toReportInfoDTO(PerformanceAccountTemplateDataEntity entity);
	
	@Mapping(target = "targetPeriodType", source = "entity.targetPeriod.businessId")
	@Mapping(target = "targetPeriodName", source = "entity.targetPeriod.name")
	AccountPerformanceAccountTemplateDataReportDetailsDTO toReportDetailsDTO(PerformanceAccountTemplateDataEntity entity);
}
