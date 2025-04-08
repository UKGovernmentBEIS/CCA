package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesRunDetailsInfo {

	private Long runId;
	
	private String paymentRequestId;
	
    private LocalDateTime submissionDate;
    
    private BigDecimal initialTotalAmount;
    
    private BigDecimal currentTotalAmount;
}
