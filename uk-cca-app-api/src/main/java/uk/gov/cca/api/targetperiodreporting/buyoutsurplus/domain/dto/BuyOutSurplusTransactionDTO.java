package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionDTO {
    private Long id;
    private Long performanceDataId;
    private String transactionCode;
    private BigDecimal buyOutFee;
    private BuyOutSurplusPaymentStatus paymentStatus;
    private BuyOutSurplusContainer buyOutSurplusContainer;
    private String fileDocumentUuid;
    private LocalDateTime creationDate;
}
