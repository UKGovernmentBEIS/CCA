package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityTargets {

    @NotEmpty
    @Valid
    @Builder.Default
    private Map<TargetImprovementType, @NotNull @DecimalMax(value = "100") @Digits(integer = 3, fraction = 7) BigDecimal> improvements = new EnumMap<>(TargetImprovementType.class);
}
