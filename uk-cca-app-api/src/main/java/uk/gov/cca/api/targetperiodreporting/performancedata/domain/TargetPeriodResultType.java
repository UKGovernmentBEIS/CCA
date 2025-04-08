package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetPeriodResultType {

    TARGET_MET("Target Met"),
    BUY_OUT_REQUIRED("Buy-out Required"),
    SURPLUS_USED_BUY_OUT_REQUIRED("Surplus used and buy-out required"),
    SURPLUS_USED("Surplus used"),
    OUTSTANDING("Outstanding");

    private final String description;
    
    public static TargetPeriodResultType fromDescription(String description) {
        return Arrays.stream(values())
                .filter(type -> type.description.equalsIgnoreCase(description))
                .findFirst()
                .orElse(null);
    }
}
