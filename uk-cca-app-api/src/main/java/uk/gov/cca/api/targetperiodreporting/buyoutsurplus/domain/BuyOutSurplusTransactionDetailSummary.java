package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionDetailSummary {

    private Long id; //detailId
    private String facilityBusinessId;
    private TargetPeriodType targetPeriodType;
    private BuyOutSurplusChargeType chargeType;
    private LocalDateTime creationDate;
    
    private Long performanceDataId;
    private BuyOutSurplusPaymentStatus paymentStatus;
    private BigDecimal buyOutFee;
}
