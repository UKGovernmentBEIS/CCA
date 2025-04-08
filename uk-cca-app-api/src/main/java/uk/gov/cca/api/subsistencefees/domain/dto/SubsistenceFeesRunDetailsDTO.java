package uk.gov.cca.api.subsistencefees.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesRunDetailsDTO {

	private Long runId;
	
	private String paymentRequestId;
	
    private LocalDateTime submissionDate;
    
    private PaymentStatus paymentStatus;
    
    private BigDecimal initialTotalAmount;
    
    private BigDecimal currentTotalAmount;
    
    private BigDecimal outstandingTotalAmount;
    
    private Long sectorMoasCount;
    
    private Long targetUnitMoasCount;
    
}
