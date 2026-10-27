package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityTransactionTemplateData {
    private String transactionCode;
    private LocalDate invoicedPaymentDeadline;
    private BigDecimal invoicedBuyOutFee;
    private int targetPeriodTypeNumber;
    private TargetPeriodType targetPeriodType;
}
