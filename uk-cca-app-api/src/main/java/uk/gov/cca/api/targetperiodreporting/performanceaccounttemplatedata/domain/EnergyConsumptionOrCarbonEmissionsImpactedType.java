package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnergyConsumptionOrCarbonEmissionsImpactedType {
    FIXED("Fixed"),
    VARIABLE("Variable"),
    FIXED_AND_VARIABLE("Fixed and Variable");
    
    private final String description;
    
    public static EnergyConsumptionOrCarbonEmissionsImpactedType fromDescription(String descr) {
        return StringUtils.isBlank(descr) ? null : Arrays.stream(values())
                .filter(ct -> ct.description.equalsIgnoreCase(descr.trim()))
                .findFirst()
                .orElse(null);
    }
}
