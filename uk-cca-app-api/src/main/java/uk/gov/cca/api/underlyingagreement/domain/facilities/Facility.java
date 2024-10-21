package uk.gov.cca.api.underlyingagreement.domain.facilities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#status eq 'EXCLUDED') == (#excludedDate != null)}",
		message = "underlyingagreement.facilities.excludedDate")
public class Facility implements UnderlyingAgreementSection {

	@NotNull
	private FacilityStatus status;

	@PastOrPresent
	private LocalDate excludedDate;

	@JsonUnwrapped
	@NotNull
	@Valid
	private FacilityItem facilityItem;
}
