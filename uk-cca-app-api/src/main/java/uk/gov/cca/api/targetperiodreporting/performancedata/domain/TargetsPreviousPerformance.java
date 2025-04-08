package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetsPreviousPerformance {
	
    @NotNull
    @PositiveOrZero
    private Integer numOfFacilities;

    @NotNull
    private AgreementCompositionType targetType;

    @NotNull
    private MeasurementType energyCarbonUnit;

    private String throughputUnit;

    // Base year start date
    @NotNull
    private LocalDate byStartDate;

    // Base year energy
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal byEnergyCarbon;

    // Base year throughput
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal byThroughput;

    // Percent improvement target
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 9)
    private BigDecimal percentTarget;

    // +/- Tolerance on target %
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal tolerancePercentage;

    // Banked surplus from previous Target Period (tCO2e)
    // For TP6 this will be zero for all target units as they are not allowed to carry forward the surplus from TP5
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal bankedSurplus;

    // Base year performance
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal byPerformance;

    // Numerical target
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal numericalTarget;

    // +/- Tolerance on target in target units
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tolerance;
}
