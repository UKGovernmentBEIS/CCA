package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurplusCreate {

    @NotNull
    private Long accountId;

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    @PositiveOrZero
    private BigDecimal surplusGained;

    @Valid
    @NotNull
    private SurplusCreateHistory history;
}
