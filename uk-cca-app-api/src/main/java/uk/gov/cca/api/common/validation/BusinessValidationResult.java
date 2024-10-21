package uk.gov.cca.api.common.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessValidationResult {

    private boolean valid;

    @Builder.Default
    private List<? extends BusinessViolation> violations = new ArrayList<>();

    public static BusinessValidationResult valid() {
        return BusinessValidationResult.builder().valid(true).build();
    }

    public static BusinessValidationResult invalid(List<? extends BusinessViolation> violations) {
        return BusinessValidationResult.builder().valid(false).violations(violations).build();
    }
}
