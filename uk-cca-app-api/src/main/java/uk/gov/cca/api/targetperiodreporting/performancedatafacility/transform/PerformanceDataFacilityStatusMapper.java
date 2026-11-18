package uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataStatusInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {RoleTypeConstants.class})
public interface PerformanceDataFacilityStatusMapper {

	@Mapping(target = "targetPeriodName", source = "entity.targetPeriod.name")
	@Mapping(target = "targetPeriodType", source = "entity.targetPeriod.businessId")
	@Mapping(target = "reportVersion", source = "entity.lastPerformanceData.reportVersion")
	@Mapping(target = "submissionDate", source = "entity.lastPerformanceData.submissionDate")
	@Mapping(target = "lockEditable", expression = "java(RoleTypeConstants.REGULATOR.equals(roleType) && !LocalDate.now().isBefore(secondaryReportingStartDate) && entity.getLastPerformanceData().isFinal())")
	@Mapping(target = "variationIndicatorEditable", expression = "java(RoleTypeConstants.REGULATOR.equals(roleType))")
	FacilityPerformanceDataStatusInfoDTO toFacilityPerformanceDataStatusInfoDTO(PerformanceDataFacilityStatus entity, LocalDate secondaryReportingStartDate, String roleType);
}
