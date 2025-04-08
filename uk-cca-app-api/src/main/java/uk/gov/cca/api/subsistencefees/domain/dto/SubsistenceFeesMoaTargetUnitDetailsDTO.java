package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaTargetUnitDetailsDTO {

	private Long moaTargetUnitId;
		
	private String businessId;
	
	private String name;
		
	private BigDecimal initialTotalAmount;

    private LocalDateTime submissionDate;
    
    private BigDecimal facilityFee;
    
    private BigDecimal currentTotalAmount;
    
    private Long totalFacilities;
    
    private Long paidFacilities; 
}
