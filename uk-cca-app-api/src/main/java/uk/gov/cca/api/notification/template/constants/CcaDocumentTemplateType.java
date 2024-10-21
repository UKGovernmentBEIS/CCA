package uk.gov.cca.api.notification.template.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CcaDocumentTemplateType {

    // AIT_L001_Administrative_Termination
    public static final String ADMIN_TERMINATION_ADMINISTRATIVE_SUBMITTED = "ADMIN_TERMINATION_ADMINISTRATIVE_SUBMITTED";

    // AIT_L002_Regulatory_Termination_Right_Of_Appeal
    public static final String ADMIN_TERMINATION_REGULATORY_SUBMITTED = "ADMIN_TERMINATION_REGULATORY_SUBMITTED";

    // AIT_L003_Regulatory_Termination
    public static final String ADMIN_TERMINATION_REGULATORY_TERMINATED = "ADMIN_TERMINATION_REGULATORY_TERMINATED";

    // Withdrawal of termination letter
    public static final String ADMIN_TERMINATION_REGULATORY_WITHDRAWN = "ADMIN_TERMINATION_REGULATORY_WITHDRAWN";
    
    // Updated UnA template - v03
    public static final String UNDERLYING_AGREEMENT = "UNDERLYING_AGREEMENT";
    
    //AA_L001_Underlying_Agreement_Application_Refusal.docx
    public static final String UNDERLYING_AGREEMENT_REJECTED = "UNDERLYING_AGREEMENT_REJECTED";
    
    // AA_L002_Proposed_Underlying_Agreement
    public static final String UNDERLYING_AGREEMENT_ACCEPTED = "UNDERLYING_AGREEMENT_ACCEPTED";
    
    // AA_L003_Underlying_Agreement_Activated
    public static final String UNDERLYING_AGREEMENT_ACTIVATED = "UNDERLYING_AGREEMENT_ACTIVATED";
    
    // CR_L002_Variation_Approved
    public static final String UNDERLYING_AGREEMENT_VARIATION_ACCEPTED = "UNDERLYING_AGREEMENT_VARIATION_ACCEPTED";

    // CR_L001_Variation_Refused.docx
    public static final String UNDERLYING_AGREEMENT_VARIATION_REJECTED = "UNDERLYING_AGREEMENT_VARIATION_REJECTED";
}
