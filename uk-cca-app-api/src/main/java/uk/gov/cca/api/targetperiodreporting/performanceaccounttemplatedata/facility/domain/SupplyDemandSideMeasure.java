package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SupplyDemandSideMeasure {

    SUPPLY_SIDE("Supply Side"),
    DEMAND_SIDE("Demand Side");

    private final String description;

    public static SupplyDemandSideMeasure fromDescription(String descr) {
        return StringUtils.isBlank(descr) ? null : Arrays.stream(values())
                .filter(ct -> ct.description.equalsIgnoreCase(descr.trim()))
                .findFirst()
                .orElse(null);
    }
}
