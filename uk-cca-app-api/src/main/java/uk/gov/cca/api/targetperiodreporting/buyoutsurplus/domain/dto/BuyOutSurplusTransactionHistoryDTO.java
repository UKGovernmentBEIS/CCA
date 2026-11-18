package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistoryPayload;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionHistoryDTO {
    
    private Long id;
    private String submitter;
    private LocalDateTime submissionDate;
    private BuyOutSurplusTransactionHistoryPayload payload;
}
