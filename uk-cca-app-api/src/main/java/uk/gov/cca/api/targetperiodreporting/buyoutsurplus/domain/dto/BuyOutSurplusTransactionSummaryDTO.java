package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyOutSurplusTransactionSummaryDTO {

	private String transactionCode;

	private BuyOutSurplusPaymentStatus paymentStatus;

	private BigDecimal initialAmount;

	private BigDecimal currentAmount;
}
