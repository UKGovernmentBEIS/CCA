package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurplusCreate {

    @NotNull
    private Long resourceId;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    @PositiveOrZero
    private BigDecimal surplusGained;

    @Valid
    @NotNull
    private SurplusCreateHistory history;
}
