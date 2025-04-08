package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActualTargetPeriodPerformance implements TP6PerformanceDataSection {

    // Identifier
    @NotBlank
    private String actualTuIdentifier;

    // Throughput
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal actualThroughput;

    @Builder.Default
    private Map<FixedConversionFactor, @NotNull @Digits(integer = Integer.MAX_VALUE, fraction = 20) BigDecimal> energyData = new EnumMap<>(FixedConversionFactor.class);

    @Valid
    @Builder.Default
    private List<OtherFuel> carbonFactors = IntStream.rangeClosed(1, 11)
            .mapToObj(i -> OtherFuel.builder()
                    .name("")
                    .conversionFactor(BigDecimal.ZERO)
                    .consumption(BigDecimal.ZERO)
                    .build())
            .toList();

    // Target Period Total Energy
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpEnergy;

    // Target Period CHP delivered electricity
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpChpDeliveredElectricity;

    // Adjusted target period throughput
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal reportingThroughput;

    // Adjusted target period throughput of Target Unit Entry
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal adjustedThroughput;
}
