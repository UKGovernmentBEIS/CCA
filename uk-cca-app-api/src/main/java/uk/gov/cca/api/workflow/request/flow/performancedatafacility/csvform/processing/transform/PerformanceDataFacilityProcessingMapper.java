package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils.PerformanceDataFacilityCalculationFunctionUtil;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Map;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataFacilityProcessingMapper {

    @Mapping(target = "targetYear", source = "payload.targetPeriodYear.targetYear")
    PerformanceDataFacilityCalculationParameters toPerformanceDataFacilityCalculationParameters(PerformanceDataFacilityProcessingRequestPayload payload,
                                                                                                PerformanceDataFacilityBaselineAndTargets baselineAndTargets);

    @AfterMapping
    default void setCalculatedParams(@MappingTarget PerformanceDataFacilityCalculationParameters parameters,
                                     PerformanceDataFacilityProcessingRequestPayload payload) {
        // Set multiplier from target period years
        BigDecimal tpMultiplier = PerformanceDataFacilityCalculationFunctionUtil
                .getTpMultiplier(payload.getTargetPeriods(), payload.getTargetPeriodType(), payload.getTargetPeriodYear().getTargetYear())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        parameters.setTpMultiplier(tpMultiplier);

        // Set Interim or facility target
        BigDecimal targetImprovement = PerformanceDataFacilityCalculationFunctionUtil
                .getTargetImprovement(payload.getReportType(), payload.getTargetPeriodType(), payload.getBaselineAndTargets());
        parameters.setTargetImprovement(targetImprovement);

        // Set last Year Per Target Period
        Map<TargetPeriodType, Integer> lastYearPerTp = PerformanceDataFacilityCalculationFunctionUtil
                .getLastYearPerTp(payload.getTargetPeriods());
        parameters.setLastYearPerTp(lastYearPerTp);
    }

    default PerformanceDataFacilityCalculationParameters toPerformanceDataFacilityCalculationParameters(PerformanceDataFacilityProcessingRequestPayload payload) {
        return toPerformanceDataFacilityCalculationParameters(payload, payload.getBaselineAndTargets());
    }

    @Mapping(target = "facilityId", source = "requestPayload.facility.id")
    @Mapping(target = "targetPeriodYear", source = "requestPayload.targetPeriodYear.targetYear")
    PerformanceDataFacility toPerformanceDataFacility(PerformanceDataFacilityProcessingRequestPayload requestPayload, PerformanceDataFacilityContainer data);
}
