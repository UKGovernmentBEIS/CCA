package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesRunSearchResultInfo {

	private Long runId;
	
	private String paymentRequestId;

    private LocalDateTime submissionDate;
    
    private BigDecimal currentTotalAmount;
    
    private BigDecimal facilityOutstandingAmount;
    
    private BigDecimal receivedAmount;
}
