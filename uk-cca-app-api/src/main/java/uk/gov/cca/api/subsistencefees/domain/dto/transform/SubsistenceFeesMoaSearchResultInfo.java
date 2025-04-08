package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaSearchResultInfo {

	private Long moaId;
	
	private String transactionId;
	
	private String businessId;
	
	private String name;
    
    private BigDecimal currentTotalAmount;
    
    private BigDecimal facilityOutstandingAmount;
    
    private BigDecimal receivedAmount;
    
    private LocalDateTime submissionDate;
}
