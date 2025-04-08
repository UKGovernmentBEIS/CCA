package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isCoveredByUkets) == (#uketsId != null)}", 
		message = "underlyingagreement.facilities.facilitydetails.isCoveredByUkets")
@SpELExpression(expression = "{(#applicationReason eq 'CHANGE_OF_OWNERSHIP') == (#previousFacilityId != null)}", 
		message = "underlyingagreement.facilities.facilitydetails.applicationReason")
public class FacilityDetails {

	@NotNull
	@Size(max = 255)
	private String name;
	
	@NotNull
	private Boolean isCoveredByUkets;
	
	@Size(max = 255)
	private String uketsId;
	
	@NotNull
	private ApplicationReasonType applicationReason;
	
	@Size(max = 255)
	@Pattern(regexp = "^[A-Z0-9_]+-F\\d{5}$", message = "underlyingagreement.facilities.facilitydetails.previousFacilityId")
	private String previousFacilityId;
	
	@NotNull
	@Valid
	private AccountAddressDTO facilityAddress;
}
