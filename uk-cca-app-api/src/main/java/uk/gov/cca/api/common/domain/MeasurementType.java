package uk.gov.cca.api.common.domain;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeasurementType {
    ENERGY_KWH("Energy (kWh)", MeasurementCategoryType.ENERGY, "kWh"),
    ENERGY_MWH("Energy (MWh)", MeasurementCategoryType.ENERGY, "MWh"),
    ENERGY_GJ("Energy (GJ)", MeasurementCategoryType.ENERGY, "GJ"),
    CARBON_KG("Carbon (kg)", MeasurementCategoryType.CARBON, "kg"),
    CARBON_TONNE("Carbon (tonne)", MeasurementCategoryType.CARBON, "tonne");

    private final String description;
    private final MeasurementCategoryType category;
    private final String unit;

    public static MeasurementType getMeasurementTypeByUnit(String unit) {
        return Arrays.stream(MeasurementType.values())
                .filter(targetUnit -> targetUnit.getUnit().equalsIgnoreCase(unit))
                .findFirst().orElse(null);
    }
}
