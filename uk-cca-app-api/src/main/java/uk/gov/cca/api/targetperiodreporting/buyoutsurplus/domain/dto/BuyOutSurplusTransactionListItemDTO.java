package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionListItemDTO {
    private Long id;
    private String accountBusinessId;
    private String operatorName;
    private String transactionCode;
    private LocalDateTime creationDate;
    private SchemeVersion schemeVersion;
    private PerformanceDataResourceType resourceType;
    private BuyOutSurplusPaymentStatus paymentStatus;
    private BigDecimal buyOutFee;

    public BuyOutSurplusTransactionListItemDTO(Long id, String accountBusinessId, String operatorName, String transactionCode,
                                               LocalDateTime creationDate, SchemeVersion schemeVersion, PerformanceDataResourceType resourceType,
                                               String paymentStatus, BigDecimal buyOutFee) {
        this.id = id;
        this.accountBusinessId = accountBusinessId;
        this.operatorName = operatorName;
        this.transactionCode = transactionCode;
        this.creationDate = creationDate;
        this.schemeVersion = schemeVersion;
        this.resourceType = resourceType;
        this.paymentStatus = BuyOutSurplusPaymentStatus.valueOf(paymentStatus);
        this.buyOutFee = buyOutFee;
    }
}
