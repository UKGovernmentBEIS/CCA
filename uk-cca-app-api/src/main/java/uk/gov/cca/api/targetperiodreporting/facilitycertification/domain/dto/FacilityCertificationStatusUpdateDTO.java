package uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{((#certificationStatus == 'CERTIFIED') == (#startDate != null))}",
		message = "facilityCertification.status.startDate.invalid")
public class FacilityCertificationStatusUpdateDTO {

	@NotNull
	private Long certificationPeriodId;

	@NotNull
	private FacilityCertificationStatus certificationStatus;

	@PastOrPresent
	private LocalDate startDate;
}
