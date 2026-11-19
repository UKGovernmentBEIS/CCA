package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataContainerMapperTest {

    private final EmissionsTradingSchemeDataContainerMapper mapper = Mappers.getMapper(EmissionsTradingSchemeDataContainerMapper.class);

    @Test
    void toEmissionsTradingSchemeDataContainer() {
        String uketsId = "uketsId";
        MeasurementType measurementType = MeasurementType.ENERGY_KWH;
        EmissionsTradingSchemeDataInputDetails emissionsTradingSchemeDataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(6500000))
                                .build())
                        .build())
                .build();

        EmissionsTradingSchemeDataContainer expected = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .uketsId(uketsId)
                .isUltraSmallEmitter(true)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                                .name(NATURAL_GAS.getDescription())
                                .fixedConversionFactorCode(NATURAL_GAS)
                                .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, MeasurementType.ENERGY_KWH))
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryConversionFactor(BigDecimal.ONE)
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .build()
                ))
                .build();


        // invoke
        EmissionsTradingSchemeDataContainer actual = mapper.toEmissionsTradingSchemeDataContainer(emissionsTradingSchemeDataInputDetails, measurementType, uketsId);

        // verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toEmissionsTradingSchemeDataInputDetails() {
        String uketsId = "uketsId";
        MeasurementType measurementType = MeasurementType.ENERGY_KWH;

        EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .uketsId(uketsId)
                .isUltraSmallEmitter(true)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                                .name(NATURAL_GAS.getDescription())
                                .fixedConversionFactorCode(NATURAL_GAS)
                                .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, MeasurementType.ENERGY_KWH))
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryConversionFactor(BigDecimal.ONE)
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .build()
                ))
                .build();

        EmissionsTradingSchemeDataInputDetails expected = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .build())
                .build();

        // invoke
        EmissionsTradingSchemeDataInputDetails actual = mapper.toEmissionsTradingSchemeDataInputDetails(dataContainer);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}
