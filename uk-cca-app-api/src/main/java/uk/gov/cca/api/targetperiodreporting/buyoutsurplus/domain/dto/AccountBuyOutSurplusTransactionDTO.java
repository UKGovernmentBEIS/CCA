package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountBuyOutSurplusTransactionDTO extends BuyOutSurplusTransactionDTO {
    // Details
    private Long detailId;
    private TargetPeriodResultType targetPeriodResultType;
    private String reportVersion;
    private PerformanceDataSubmissionType submissionType;
    
    // Primary/Secondary Buy-Out
    private BigDecimal priBuyOutCarbon;
    private BigDecimal priBuyOutCost;
    private BigDecimal invoicedSurplusGained;
    private BigDecimal invoicedPreviousPaidFees;
    private BigDecimal buyOutFee;
}
