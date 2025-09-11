package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain;

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
public class BuyOutSurplusRunSummary {

    @NotNull
    @PositiveOrZero
    private Long totalAccounts;

    @NotNull
    @PositiveOrZero
    private Long failedAccounts;

    @NotNull
    @PositiveOrZero
    private Long buyOutTransactions;

    @NotNull
    @PositiveOrZero
    private Long refundedTransactions;
}
