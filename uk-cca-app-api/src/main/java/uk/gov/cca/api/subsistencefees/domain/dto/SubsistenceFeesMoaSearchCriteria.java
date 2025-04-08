package uk.gov.cca.api.subsistencefees.domain.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubsistenceFeesMoaSearchCriteria extends SubsistenceFeesSearchCriteria {
	
	@NotNull
	private MoaType moaType;
	
	private PaymentStatus paymentStatus;
}
