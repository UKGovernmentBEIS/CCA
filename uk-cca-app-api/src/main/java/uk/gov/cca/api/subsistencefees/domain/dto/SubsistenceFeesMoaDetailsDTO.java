package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaDetailsDTO {

	private Long moaId;
	
	private String transactionId;
	
	private String businessId;
	
	private String name;
	
	private FileInfoDTO moaDocument;
	
    private LocalDateTime submissionDate;
    
    private PaymentStatus paymentStatus;
    
    private Long totalFacilities;
    
    private Long paidFacilities;
    
    private BigDecimal initialTotalAmount;
    
    private BigDecimal currentTotalAmount;
    
    private BigDecimal receivedAmount;
    
    private BigDecimal facilityFee;
    
    private Long moaTargetUnitId;
}
