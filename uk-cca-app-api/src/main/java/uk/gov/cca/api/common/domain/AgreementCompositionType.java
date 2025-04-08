package uk.gov.cca.api.common.domain;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgreementCompositionType {
    ABSOLUTE("Absolute"),
    RELATIVE("Relative"),
    NOVEM("Novem");

    private final String description;
    
    public static AgreementCompositionType fromDescription(String description) {
        return Arrays.stream(values())
                .filter(type -> type.description.equalsIgnoreCase(description))
                .findFirst()
                .orElse(null);
    }
}
