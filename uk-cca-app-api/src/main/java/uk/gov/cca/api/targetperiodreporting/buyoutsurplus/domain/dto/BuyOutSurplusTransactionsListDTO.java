package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BuyOutSurplusTransactionsListDTO {
    private List<BuyOutSurplusTransactionListItemDTO> transactions;
    private Long total;
}
