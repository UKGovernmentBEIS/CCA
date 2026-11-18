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
    
    // CCA2 agreement
    public static final String UNDERLYING_AGREEMENT_CCA2 = "UNDERLYING_AGREEMENT_CCA2";
    
    // CCA3 agreement
    public static final String UNDERLYING_AGREEMENT_CCA3 = "UNDERLYING_AGREEMENT_CCA3";
    
    //AA_L001_Underlying_Agreement_Application_Refusal.docx
    public static final String UNDERLYING_AGREEMENT_REJECTED = "UNDERLYING_AGREEMENT_REJECTED";
    
    // CCA3 variation proposed agreement cover letter.docx
    public static final String UNDERLYING_AGREEMENT_ACCEPTED = "UNDERLYING_AGREEMENT_ACCEPTED";
    
    // AA_L003_Underlying_Agreement_Activated
    public static final String UNDERLYING_AGREEMENT_ACTIVATED = "UNDERLYING_AGREEMENT_ACTIVATED";
    
    // CCA3 variation proposed agreement cover letter
    public static final String UNDERLYING_AGREEMENT_VARIATION_ACCEPTED = "UNDERLYING_AGREEMENT_VARIATION_ACCEPTED";

    // CR_L001_Variation_Refused.docx
    public static final String UNDERLYING_AGREEMENT_VARIATION_REJECTED = "UNDERLYING_AGREEMENT_VARIATION_REJECTED";

    // CCA3 variation acknowledgement letter.docx
    public static final String UNDERLYING_AGREEMENT_VARIATION_COMPLETED = "UNDERLYING_AGREEMENT_VARIATION_COMPLETED";

    // CCA3 variation termination letter.docx
    public static final String UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION = "UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION";

    // SC_L002_Sector_MoA
    public static final String SECTOR_MOA = "SECTOR_MOA";

    // SC_L001_Target_Unit_MoA
    public static final String TARGET_UNIT_MOA = "TARGET_UNIT_MOA";

    // L001_Initial Buy-Out MoA.docx
    public static final String PRIMARY_BUY_OUT = "PRIMARY_BUY_OUT";

    // L002_Secondary Buy-Out MoA.docx
    public static final String SECONDARY_BUY_OUT = "SECONDARY_BUY_OUT";

    // L003_Secondary Buy-Out Overpayment Letter v01.docx
    public static final String SECONDARY_OVERPAYMENT_BUY_OUT = "SECONDARY_OVERPAYMENT_BUY_OUT";

    // Buyout Refund Claim Form.docx
    public static final String REFUND_CLAIM_FORM_BUY_OUT = "REFUND_CLAIM_FORM_BUY_OUT";

    // CCA2 extension cover letter
    public static final String EXTENSION_NOTICE_CCA2 = "EXTENSION_NOTICE_CCA2";

    // CCA3 migration proposed agreement cover letter.docx
    public static final String MIGRATION_UNDERLYING_AGREEMENT_ACCEPTED_CCA3 = "MIGRATION_UNDERLYING_AGREEMENT_ACCEPTED_CCA3";

    // CCA3 migration activated agreement cover letter.docx
    public static final String MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3 = "MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3";
}
