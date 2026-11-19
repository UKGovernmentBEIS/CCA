package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CsvRowColumns {

    FACILITY_ID(0),
    CATEGORY(1),
    SIDE_MEASURE(2),
    SAVING_ACTIONS(3),
    IMPLEMENTATION_REASONS(4),
    IMPLEMENTATION_DATE(5),
    FIXED_ENERGY_EMISSIONS_IMPACTED(6),
    ENERGY_EMISSIONS_IMPACTED(7),
    EXPECTED_EXTENT_IMPLEMENTED(8),
    EXPECTED_SAVINGS_IMPLEMENTED(9),
    NOTES(10);

    private final int rowNumber;
}
