package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SupplyDemandSideMeasure {

    SUPPLY_SIDE("Supply Side"),
    DEMAND_SIDE("Demand Side");

    private final String description;

}
