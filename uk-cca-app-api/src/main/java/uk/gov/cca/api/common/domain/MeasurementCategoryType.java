package uk.gov.cca.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeasurementCategoryType {
    ENERGY("Energy"),
    CARBON("Carbon");

    private String description;
}
