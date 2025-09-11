package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TargetType {
    ABSOLUTE("Absolute"),
    RELATIVE("Relative"),
    NOVEM_ENERGY("Novem Energy"),
    NOVEM_CARBON("Novem Carbon");
    
    private String description;
    
    public static TargetType fromDescription(String description) {
        return Arrays.stream(values())
                .filter(type -> type.description.equalsIgnoreCase(description))
                .findFirst()
                .orElse(null);
    }
}
