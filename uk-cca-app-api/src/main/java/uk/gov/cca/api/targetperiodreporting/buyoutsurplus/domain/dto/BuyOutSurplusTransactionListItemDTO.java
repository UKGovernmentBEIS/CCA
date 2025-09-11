package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

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
    private BuyOutSurplusPaymentStatus paymentStatus;
    private BigDecimal buyOutFee;
}
