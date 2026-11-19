package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionReceivedAmountHistoryDTO {

    private Long id;

    private String submitter;

    private LocalDateTime submissionDate;

    private BigDecimal adjustmentAmount;

    private String comments;

    private LocalDate paymentDate;

    @Builder.Default
    private Map<UUID, String> evidenceFiles = new HashMap<>();
}