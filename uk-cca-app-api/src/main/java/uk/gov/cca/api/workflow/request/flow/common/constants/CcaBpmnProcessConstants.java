package uk.gov.cca.api.workflow.request.flow.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Encapsulates domain related to BPMN Process for CCA workflows
 */
@UtilityClass
public class CcaBpmnProcessConstants {
	
	public static final String EXPIRATION_DATE = "ExpirationDate";

	// admin termination
    public static final String ADMIN_TERMINATION_OUTCOME = "adminTerminationOutcome";
    public static final String IS_REGULATORY_REASON = "isRegulatoryReason";
    public static final String ADMIN_TERMINATION_FINAL_DECISION = "adminTerminationFinalDecision";
    public static final String ADMIN_TERMINATION_EXPIRATION_DATE = CcaRequestExpirationKey.ADMIN_TERMINATION + EXPIRATION_DATE;
    
    // underlying agreement
    public static final String UNDERLYING_AGREEMENT_OUTCOME = "underlyingAgreementOutcome";
    public static final String UNDERLYING_AGREEMENT_EXPIRATION_DATE = CcaRequestExpirationKey.UNDERLYING_AGREEMENT + EXPIRATION_DATE;
    
 	// underlying agreement variation
    public static final String UNDERLYING_AGREEMENT_VARIATION_OUTCOME = "underlyingAgreementVariationOutcome";
    public static final String UNDERLYING_AGREEMENT_VARIATION_EXPIRATION_DATE = CcaRequestExpirationKey.UNDERLYING_AGREEMENT_VARIATION + EXPIRATION_DATE;
}
