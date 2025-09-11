package uk.gov.cca.api.authorization.ccaauth.regulator.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CcaRegulatorPermissionGroup {

    public static final String MANAGE_SECTOR_ASSOCIATIONS = "MANAGE_SECTOR_ASSOCIATIONS";
    public static final String MANAGE_SECTOR_USERS = "MANAGE_SECTOR_USERS";
    public static final String MANAGE_OPERATOR_USERS = "MANAGE_OPERATOR_USERS";

    /**
     * Add permission to Regulator for Admin Termination Work Flow
     */
    public static final String ADMIN_TERMINATION_SUBMISSION = "ADMIN_TERMINATION_SUBMISSION";

    public static final String ADMIN_TERMINATION_PEER_REVIEW = "ADMIN_TERMINATION_PEER_REVIEW";

    public static final String UNDERLYING_AGREEMENT_APPLICATION_REVIEW = "UNDERLYING_AGREEMENT_APPLICATION_REVIEW";

    public static final String UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW = "UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW";

    public static final String UNDERLYING_AGREEMENT_VARIATION_REVIEW = "UNDERLYING_AGREEMENT_VARIATION_REVIEW";

    public static final String UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW = "UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW";

}
