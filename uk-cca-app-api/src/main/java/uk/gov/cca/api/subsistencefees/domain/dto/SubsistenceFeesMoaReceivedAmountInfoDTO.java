package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubsistenceFeesMoaReceivedAmountInfoDTO {

    private String transactionId;

    private String businessId;

    private String name;

    private PaymentStatus paymentStatus;

    private BigDecimal currentTotalAmount;

    private BigDecimal receivedAmount;

    private List<SubsistenceFeesMoaReceivedAmountHistoryDTO> receivedAmountHistoryList;
}
