package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityBuyOutSurplusTransactionDTO extends BuyOutSurplusTransactionDTO {
    private BigDecimal expectedAmount;
    private BigDecimal receivedAmount;
    private BigDecimal amountAllocated;
    private int expectedMarkedFacilities;
    private int actualMarkedFacilities;
    private boolean hasBulkAction;
}
