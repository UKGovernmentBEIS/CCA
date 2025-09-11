package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusResult {

    @NotNull
    private Long performanceDataId;

    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;

    private String transactionCode;

    private BuyOutSurplusPaymentStatus paymentStatus;

    private String fileDocumentUuid;
}
