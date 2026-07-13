package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils.PerformanceDataFacilityCalculationFunctionUtil;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataFacilityDigitalFormSubmitMapper {

    @Mapping(target = "targetYear", source = "payload.targetPeriodYear")
    PerformanceDataFacilityCalculationParameters toPerformanceDataFacilityCalculationParameters(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload payload,
                                                                                                PerformanceDataFacilityBaselineAndTargets baselineAndTargets,
                                                                                                List<TargetPeriodDetailsDTO> targetPeriods);

    @AfterMapping
    default void setCalculatedParams(@MappingTarget PerformanceDataFacilityCalculationParameters parameters,
                                     PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload payload,
                                     List<TargetPeriodDetailsDTO> targetPeriods) {
        // Set multiplier from target period years
        BigDecimal tpMultiplier = PerformanceDataFacilityCalculationFunctionUtil
                .getTpMultiplier(targetPeriods, payload.getTargetPeriodType(), payload.getTargetPeriodYear())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        parameters.setTpMultiplier(tpMultiplier);

        // Set Interim or facility target
        BigDecimal targetImprovement = PerformanceDataFacilityCalculationFunctionUtil
                .getTargetImprovement(payload.getReportType(), payload.getTargetPeriodType(),  payload.getReferenceData().getBaselineAndTargets());
        parameters.setTargetImprovement(targetImprovement);

        // Set last Year Per Target Period
        Map<TargetPeriodType, Integer> lastYearPerTp = PerformanceDataFacilityCalculationFunctionUtil
                .getLastYearPerTp(targetPeriods);
        parameters.setLastYearPerTp(lastYearPerTp);
    }

    default PerformanceDataFacilityCalculationParameters toPerformanceDataFacilityCalculationParameters(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload payload,
                                                                                                        List<TargetPeriodDetailsDTO> targetPeriods) {
        return toPerformanceDataFacilityCalculationParameters(payload, payload.getReferenceData().getBaselineAndTargets(), targetPeriods);
    }

    @Mapping(target = "calculatedResults", ignore = true)
    PerformanceDataFacilityInputData toPerformanceDataFacilityInputData(PerformanceDataFacilityContainer container);

    @AfterMapping
    default void setFuels(@MappingTarget PerformanceDataFacilityInputData data, PerformanceDataFacilityContainer container) {
        // Transform standards fuels
        Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> standardFuels = new EnumMap<>(PerformanceDataFacilityFixedConversionFactor.class);
        container.getEnergyFuelDetails().getFuels().stream()
                .filter(f -> ObjectUtils.isNotEmpty(f.getFixedConversionFactorCode()))
                .sorted(Comparator.comparing(PerformanceDataFacilityFuel::getFixedConversionFactorCode))
                .forEach(f-> standardFuels.put(f.getFixedConversionFactorCode(), toPerformanceDataFacilityFuelEnergyConsumption(f)));

        data.getEnergyFuelDetails().setStandardFuels(standardFuels);

        // Transform non standards fuels
        List<PerformanceDataFacilityNonStandardFuel> nonStandardFuels = new ArrayList<>();
        container.getEnergyFuelDetails().getFuels().stream()
                .filter(f -> ObjectUtils.isEmpty(f.getFixedConversionFactorCode()))
                .map(this::toPerformanceDataFacilityNonStandardFuel)
                .forEach(nonStandardFuels::add);

        data.getEnergyFuelDetails().setNonStandardFuels(nonStandardFuels);
    }

    PerformanceDataFacilityFuelEnergyConsumption toPerformanceDataFacilityFuelEnergyConsumption(PerformanceDataFacilityFuel fuel);

    PerformanceDataFacilityNonStandardFuel toPerformanceDataFacilityNonStandardFuel(PerformanceDataFacilityFuel fuel);
}
