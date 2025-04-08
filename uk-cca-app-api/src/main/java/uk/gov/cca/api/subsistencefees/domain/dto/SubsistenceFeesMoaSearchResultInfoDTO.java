package uk.gov.cca.api.subsistencefees.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaSearchResultInfoDTO {

	private Long moaId;
	
	private String transactionId;
	
	private String businessId;
	
	private String name;
    
    private PaymentStatus paymentStatus;
    
    private FacilityPaymentStatus markFacilitiesStatus;
    
    private BigDecimal currentTotalAmount;
    
    private BigDecimal outstandingTotalAmount;
    
    private LocalDateTime submissionDate;
}
