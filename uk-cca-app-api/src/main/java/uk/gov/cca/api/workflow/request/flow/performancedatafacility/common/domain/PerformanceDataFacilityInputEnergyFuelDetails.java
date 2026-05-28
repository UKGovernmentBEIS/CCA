package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityInputEnergyFuelDetails {

    @Builder.Default
    private Map<PerformanceDataFacilityFixedConversionFactor, @Valid @NotNull PerformanceDataFacilityFuelEnergyConsumption> standardFuels = new EnumMap<>(PerformanceDataFacilityFixedConversionFactor.class);

    @Size(max = 10)
    @Builder.Default
    private List<@Valid @NotNull PerformanceDataFacilityNonStandardFuel> nonStandardFuels = new ArrayList<>();

    @NotNull
    private Boolean atLeastSeventyPercentEnergyUsed;

    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal electricitySuppliedFromCHP;

    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal throughputAdjustmentFactor;
}
