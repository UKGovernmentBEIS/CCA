package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public enum PerformanceDataFacilityFixedConversionFactor {

    GRID_ELECTRICITY("Grid electricity and electricity from combustion of a renewable fuel", BigDecimal.valueOf(0.10046), BigDecimal.valueOf(2.1)),
    NON_GRID_ELECTRICITY("Non-grid electricity from renewable sources (PV, hydro and wind)", BigDecimal.ZERO, BigDecimal.ONE),
    NATURAL_GAS("Natural gas", BigDecimal.valueOf(0.18254), BigDecimal.ONE),
    LPG("LPG", BigDecimal.valueOf(0.21449), BigDecimal.ONE),
    GAS_DIESEL_OIL("Gas oil/Diesel", BigDecimal.valueOf(0.25679), BigDecimal.ONE),
    FUEL_OIL("Fuel Oil", BigDecimal.valueOf(0.26816), BigDecimal.ONE),
    KEROSENE("Kerosene", BigDecimal.valueOf(0.24677), BigDecimal.ONE),
    COAL("Coal", BigDecimal.valueOf(0.32463), BigDecimal.ONE),
    COKE("Coke", BigDecimal.valueOf(0.429), BigDecimal.ONE),
    PETROL("Petrol", BigDecimal.valueOf(0.22719), BigDecimal.ONE),
    NITROGEN_COOLING("Nitrogen cooling", BigDecimal.valueOf(0.10046), BigDecimal.valueOf(2.1)),
    CARBON_DIOXIDE_COOLING("Carbon dioxide cooling", BigDecimal.valueOf(0.10046), BigDecimal.valueOf(2.1)),
    ETHANE("Ethane", BigDecimal.valueOf(0.19983), BigDecimal.ONE),
    NAPHTHA("Naphtha", BigDecimal.valueOf(0.23651), BigDecimal.ONE),
    PETROLEUM_COKE("Petroleum coke", BigDecimal.valueOf(0.34095), BigDecimal.ONE),
    REFINERY_GAS("Refinery gas", BigDecimal.valueOf(0.18324), BigDecimal.ONE);

    private final String description;
    private final BigDecimal factor;
    private final BigDecimal primaryFactor;

    public static BigDecimal getValueByMeasurementType(PerformanceDataFacilityFixedConversionFactor conversionFactor, MeasurementType measurementType) {
        return switch (measurementType) {
            case ENERGY_KWH, CARBON_KG, CARBON_TONNE -> conversionFactor.factor.setScale(20, RoundingMode.HALF_UP);
            case ENERGY_GJ -> conversionFactor.factor.multiply(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(3.6), 20, RoundingMode.HALF_UP);
            case ENERGY_MWH -> conversionFactor.factor.multiply(BigDecimal.valueOf(1000)).setScale(20, RoundingMode.HALF_UP);
        };
    }
}