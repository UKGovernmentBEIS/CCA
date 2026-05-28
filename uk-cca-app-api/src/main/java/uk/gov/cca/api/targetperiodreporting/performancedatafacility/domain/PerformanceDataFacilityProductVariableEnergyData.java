package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityProductVariableEnergyData {

    @NotBlank
    @Size(max = 255)
    private String productName;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal targetImprovement;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal actualThroughput;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal adjustedThroughput;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal targetEnergy;
}
