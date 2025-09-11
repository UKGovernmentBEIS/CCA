package uk.gov.cca.api.subsistencefees.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaFacilitySearchResultInfoDTO {

	private Long moaFacilityId;
	
	private String facilityId;
	
	private String facilityName;
        
    private FacilityPaymentStatus markFacilitiesStatus;
    
    private LocalDate paymentDate;
    
    private boolean hasHistory;
}
