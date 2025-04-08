package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.math.MathContext;

@Getter
@AllArgsConstructor
public enum FixedConversionFactor {
    ELECTRICITY("Electricity", BigDecimal.valueOf(0.0546)),
    NATURAL_GAS("Natural Gas", BigDecimal.valueOf(0.0505)),
    FUEL_OIL("Fuel Oil", BigDecimal.valueOf(0.0732)),
    COAL("Coal", BigDecimal.valueOf(0.0794)),
    COKE("Coke", BigDecimal.valueOf(0.117)),
    LPG("LPG", BigDecimal.valueOf(0.0585)),
    ETHANE("Ethane", BigDecimal.valueOf(0.0545)),
    KEROSENE("Kerosene", BigDecimal.valueOf(0.0673)),
    PETROL("Petrol", BigDecimal.valueOf(0.0643)),
    GAS_DIESEL_OIL("Gas Oil/ Diesel Oil", BigDecimal.valueOf(0.0758)),
    NAPHTHA("Naphtha", BigDecimal.valueOf(0.0646)),
    PETROLEUM_COKE("Petroleum Coke", BigDecimal.valueOf(0.0908)),
    REFINERY_GAS("Refinery Gas", BigDecimal.valueOf(0.0671));

    private final String description;
    private final BigDecimal tp6Value;

    public static BigDecimal getTP6ValueByMeasurementType(FixedConversionFactor conversionFactor, MeasurementType measurementType) {
        return switch (measurementType) {
            case ENERGY_MWH, CARBON_KG, CARBON_TONNE -> conversionFactor.tp6Value;
            case ENERGY_GJ -> conversionFactor.tp6Value.divide(BigDecimal.valueOf(3.6), MathContext.DECIMAL128);
            case ENERGY_KWH -> conversionFactor.tp6Value.divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128);
        };
    }
}
