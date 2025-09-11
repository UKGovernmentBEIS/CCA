package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionDetailsDTO {
    // Details
    private Long id;
    private String transactionCode;
    private String accountBusinessId;
    private String operatorName;
    private TargetPeriodType targetPeriodType;
    private TargetPeriodResultType targetPeriodResultType;
    private String reportVersion;
    private PerformanceDataSubmissionType submissionType;
    private FileInfoDTO fileInfoDTO;
    private LocalDateTime creationDate;
    private LocalDate dueDate;
    private BuyOutSurplusPaymentStatus paymentStatus;
    
    // Primary/Secondary Buy-Out
    private BuyOutSurplusChargeType chargeType;
    private BigDecimal priBuyOutCarbon;
    private BigDecimal priBuyOutCost;
    private BigDecimal invoicedBuyOutFee;
    private BigDecimal invoicedSurplusGained;
    private BigDecimal invoicedPreviousPaidFees;
    private BigDecimal buyOutFee;
}
