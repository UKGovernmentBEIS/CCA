package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public enum PerformanceDataFacilityFixedConversionFactor {

    GRID_ELECTRICITY("Grid electricity and electricity from combustion of a renewable fuel", BigDecimal.valueOf(100.46), BigDecimal.valueOf(2.1)),
    NON_GRID_ELECTRICITY("Non-grid electricity from renewable sources (PV, hydro and wind)", BigDecimal.ZERO, BigDecimal.ONE),
    NATURAL_GAS("Natural gas", BigDecimal.valueOf(182.54), BigDecimal.ONE),
    LPG("LPG", BigDecimal.valueOf(214.49), BigDecimal.ONE),
    GAS_DIESEL_OIL("Gas oil/Diesel", BigDecimal.valueOf(256.79), BigDecimal.ONE),
    FUEL_OIL("Fuel Oil", BigDecimal.valueOf(268.16), BigDecimal.ONE),
    KEROSENE("Kerosene", BigDecimal.valueOf(246.77), BigDecimal.ONE),
    COAL("Coal", BigDecimal.valueOf(324.63), BigDecimal.ONE),
    COKE("Coke", BigDecimal.valueOf(429), BigDecimal.ONE),
    PETROL("Petrol", BigDecimal.valueOf(227.19), BigDecimal.ONE),
    NITROGEN_COOLING("Nitrogen cooling", BigDecimal.valueOf(100.46), BigDecimal.valueOf(2.1)),
    CARBON_DIOXIDE_COOLING("Carbon dioxide cooling", BigDecimal.valueOf(100.46), BigDecimal.valueOf(2.1)),
    ETHANE("Ethane", BigDecimal.valueOf(199.83), BigDecimal.ONE),
    NAPHTHA("Naphtha", BigDecimal.valueOf(236.51), BigDecimal.ONE),
    PETROLEUM_COKE("Petroleum coke", BigDecimal.valueOf(340.95), BigDecimal.ONE),
    REFINERY_GAS("Refinery gas", BigDecimal.valueOf(183.24), BigDecimal.ONE);

    private final String description;
    private final BigDecimal factor;
    private final BigDecimal primaryFactor;

    public static BigDecimal getValueByMeasurementType(PerformanceDataFacilityFixedConversionFactor conversionFactor, MeasurementType measurementType) {
        return switch (measurementType) {
            case ENERGY_MWH, CARBON_TONNE -> conversionFactor.factor.setScale(5, RoundingMode.HALF_UP);
            case ENERGY_GJ -> conversionFactor.factor.divide(BigDecimal.valueOf(3.6), 5, RoundingMode.HALF_UP);
            case ENERGY_KWH, CARBON_KG -> conversionFactor.factor.divide(BigDecimal.valueOf(1000), 5, RoundingMode.HALF_UP);
        };
    }
}
