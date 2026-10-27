package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO {
    private Long id;
    private String facilityBusinessId;
    private String siteName;
    private BuyOutSurplusPaymentStatus paymentStatus;
    private BigDecimal invoicedBuyFee;
    private BigDecimal buyOutFee;
    private boolean hasHistory;
}
