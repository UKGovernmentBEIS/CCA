package uk.gov.cca.api.underlyingagreement.domain.facilities;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isConnectedToExistingFacility) == (#adjacentFacilityId != null)}", 
		message = "underlyingagreement.facilities.authorisation.isConnectedToExistingFacility")
@SpELExpression(expression = "{(#agreementType eq 'ENVIRONMENTAL_PERMITTING_REGULATIONS') == (#erpAuthorisationExists != null)}", 
		message = "underlyingagreement.facilities.authorisation.agreementType")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#erpAuthorisationExists) == (#authorisationNumber != null)}", 
		message = "underlyingagreement.facilities.authorisation.authorisationNumber")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#erpAuthorisationExists) == (#regulatorName != null)}", 
		message = "underlyingagreement.facilities.authorisation.regulatorName")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#erpAuthorisationExists) == (#permitFile != null)}", 
		message = "underlyingagreement.facilities.authorisation.permitFile")
public class EligibilityDetailsAndAuthorisation {

	@NotNull
	private Boolean isConnectedToExistingFacility;
	
	@Size(max = 255)
	private String adjacentFacilityId;
	
	@NotNull
	private AgreementType agreementType;
	
	private Boolean erpAuthorisationExists;
	
	@Size(max = 255)
	private String authorisationNumber;
	
	private RegulatorNameType regulatorName;
	
	private UUID permitFile;
}
