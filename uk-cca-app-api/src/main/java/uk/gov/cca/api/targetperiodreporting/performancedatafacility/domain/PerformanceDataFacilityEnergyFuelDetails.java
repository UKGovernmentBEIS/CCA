package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityEnergyFuelDetails {

    @Builder.Default
    private List<@Valid @NotNull PerformanceDataFacilityFuel> fuels = new ArrayList<>();

    @NotNull
    private Boolean atLeastSeventyPercentEnergyUsed;

    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal electricitySuppliedFromCHP;

    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal throughputAdjustmentFactor;
}
