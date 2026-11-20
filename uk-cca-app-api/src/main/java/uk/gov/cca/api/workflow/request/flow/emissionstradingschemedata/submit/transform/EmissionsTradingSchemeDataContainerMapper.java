package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.common.utils.PerformanceFuelEnergyCalculationCommonFunctionUtil;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = PerformanceFuelEnergyCalculationCommonFunctionUtil.class)
public interface EmissionsTradingSchemeDataContainerMapper {

    @Mapping(target = "calculatedResults", expression = "java(data.getEnergyFuelDetails() != null ? data.getEnergyFuelDetails().getCalculatedResults() : null)")
    EmissionsTradingSchemeDataContainer toEmissionsTradingSchemeDataContainer(EmissionsTradingSchemeDataInputDetails data, MeasurementType measurementType, String uketsId);

    @Mapping(target = "provideFuelDetails", expression = "java(!container.getIsUltraSmallEmitter() ? null : container.getIsUltraSmallEmitter() && container.getFuels() != null && container.getFuels().size() > 0)")
    EmissionsTradingSchemeDataInputDetails toEmissionsTradingSchemeDataInputDetails(EmissionsTradingSchemeDataContainer container);

    @AfterMapping
    default void setFuels(@MappingTarget EmissionsTradingSchemeDataContainer container, EmissionsTradingSchemeDataInputDetails data, MeasurementType measurementType) {
        List<PerformanceDataFacilityFuel> fuels = new ArrayList<>();
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = data.getEnergyFuelDetails();

        if (energyFuelDetails == null) {
            return;
        }

        // Transform standards fuels
        energyFuelDetails.getStandardFuels().entrySet().stream()
                .map(entry -> toEmissionsTradingSchemeDataFuel(entry.getKey(), entry.getValue(), measurementType))
                .sorted(Comparator.comparing(PerformanceDataFacilityFuel::getFixedConversionFactorCode))
                .forEach(fuels::add);

        // Transform non standards fuels
        energyFuelDetails.getNonStandardFuels().stream()
                .map(this::toEmissionsTradingSchemeDataFuel)
                .forEach(fuels::add);

        container.setFuels(fuels);
    }

    @AfterMapping
    default void setEnergyFuelDetails(@MappingTarget EmissionsTradingSchemeDataInputDetails inputDetails, EmissionsTradingSchemeDataContainer container) {
        List<PerformanceDataFacilityFuel> fuels = container.getFuels();

        if (fuels == null || fuels.isEmpty()) {
            return;
        }

        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder().build();

        // Transform to standards fuels
        Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> standardFuels = fuels.stream()
                .filter(f -> f.getFixedConversionFactorCode() != null)
                .collect(Collectors.toMap(
                        PerformanceDataFacilityFuel::getFixedConversionFactorCode,
                        this::toPerformanceDataFacilityFuelEnergyConsumption
                ));

        energyFuelDetails.setStandardFuels(standardFuels);

        // Transform to non standards fuels
        List<PerformanceDataFacilityNonStandardFuel> nonStandardFuels = fuels.stream()
                .filter(f -> f.getFixedConversionFactorCode() == null)
                .map(this::toPerformanceDataFacilityNonStandardFuel)
                .toList();

        energyFuelDetails.setNonStandardFuels(nonStandardFuels);

        // set inputDetails
        inputDetails.setEnergyFuelDetails(energyFuelDetails);
    }

    PerformanceDataFacilityFuelEnergyConsumption toPerformanceDataFacilityFuelEnergyConsumption(PerformanceDataFacilityFuel fuel);

    PerformanceDataFacilityNonStandardFuel toPerformanceDataFacilityNonStandardFuel(PerformanceDataFacilityFuel fuel);

    @Mapping(target = "primaryConversionFactor", expression = "java(PerformanceFuelEnergyCalculationCommonFunctionUtil.NON_STANDARD_FUEL_PRIMARY_FACTOR)")
    @Mapping(target = "conversionFactor", expression = "java(nonStandardFuel.getConversionFactor())")
    PerformanceDataFacilityFuel toEmissionsTradingSchemeDataFuel(PerformanceDataFacilityNonStandardFuel nonStandardFuel);

    @Mapping(target = "name", expression = "java(fixedConversionFactorCode.getDescription())")
    @Mapping(target = "conversionFactor", expression = "java(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(fixedConversionFactorCode, measurementType))")
    @Mapping(target = "primaryConversionFactor", expression = "java(fixedConversionFactorCode.getPrimaryFactor())")
    PerformanceDataFacilityFuel toEmissionsTradingSchemeDataFuel(PerformanceDataFacilityFixedConversionFactor fixedConversionFactorCode,
                                                                 PerformanceDataFacilityFuelEnergyConsumption standardFuel,
                                                                 MeasurementType measurementType);
}
