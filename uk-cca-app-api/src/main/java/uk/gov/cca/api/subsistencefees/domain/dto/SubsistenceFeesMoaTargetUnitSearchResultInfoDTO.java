package uk.gov.cca.api.subsistencefees.domain.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaTargetUnitSearchResultInfoDTO {

	private Long moaTargetUnitId;
		
	private String businessId;
	
	private String name;
        
    private FacilityPaymentStatus markFacilitiesStatus;
    
    private BigDecimal currentTotalAmount;
        
}
