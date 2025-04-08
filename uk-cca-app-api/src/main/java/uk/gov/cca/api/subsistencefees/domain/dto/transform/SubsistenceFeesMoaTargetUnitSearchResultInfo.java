package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaTargetUnitSearchResultInfo {

	private Long moaTargetUnitId;
		
	private String businessId;
	
	private String name;
    
    private BigDecimal currentTotalAmount;
    
    private BigDecimal facilityOutstandingAmount;
}
