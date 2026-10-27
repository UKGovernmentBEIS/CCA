package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityBuyOutSurplusTransactionDetailSearchResultsDTO {
    private List<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO> transactionDetails;
    private Long total;

    public static FacilityBuyOutSurplusTransactionDetailSearchResultsDTO emptyResults() {
        return FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.builder()
                .transactionDetails(Collections.emptyList())
                .total(0L)
                .build();
    }
}
