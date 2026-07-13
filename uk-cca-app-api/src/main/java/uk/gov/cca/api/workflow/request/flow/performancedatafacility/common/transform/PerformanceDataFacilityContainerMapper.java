package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils.PerformanceDataFacilityCalculationCommonFunctionUtil;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = PerformanceDataFacilityCalculationCommonFunctionUtil.class)
public interface PerformanceDataFacilityContainerMapper {

    PerformanceDataFacilityContainer toPerformanceDataFacilityContainer(PerformanceDataFacilityBaselineAndTargets baselineAndTargets, PerformanceDataFacilityInputData data);

    @AfterMapping
    default void setFuels(@MappingTarget PerformanceDataFacilityContainer container, PerformanceDataFacilityBaselineAndTargets baselineTargets,
                          PerformanceDataFacilityInputData data) {
        List<PerformanceDataFacilityFuel> fuels = new ArrayList<>();

        // Transform standards fuels
        data.getEnergyFuelDetails().getStandardFuels().entrySet().stream()
                .map(entry ->
                        toPerformanceDataFacilityFuel(entry.getKey(), entry.getValue(), baselineTargets.getMeasurementType()))
                .sorted(Comparator.comparing(PerformanceDataFacilityFuel::getFixedConversionFactorCode))
                .forEach(fuels::add);

        // Transform non standards fuels
		data.getEnergyFuelDetails().getNonStandardFuels().stream()
				.map(this::toPerformanceDataFacilityFuel)
				.forEach(fuels::add);

        container.getEnergyFuelDetails().setFuels(fuels);
    }

    @Mapping(target = "primaryConversionFactor", expression = "java(PerformanceDataFacilityCalculationCommonFunctionUtil.NON_STANDARD_FUEL_PRIMARY_FACTOR)")
    @Mapping(target = "conversionFactor", expression = "java(nonStandardFuel.getConversionFactor())")
    PerformanceDataFacilityFuel toPerformanceDataFacilityFuel(PerformanceDataFacilityNonStandardFuel nonStandardFuel);

    @Mapping(target = "name", expression = "java(fixedConversionFactorCode.getDescription())")
    @Mapping(target = "conversionFactor", expression = "java(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(fixedConversionFactorCode, measurementType))")
    @Mapping(target = "primaryConversionFactor", expression = "java(fixedConversionFactorCode.getPrimaryFactor())")
    PerformanceDataFacilityFuel toPerformanceDataFacilityFuel(PerformanceDataFacilityFixedConversionFactor fixedConversionFactorCode,
                                                              PerformanceDataFacilityFuelEnergyConsumption standardFuel,
                                                              MeasurementType measurementType);
}
