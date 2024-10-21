package uk.gov.cca.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uk.gov.netz.api.common.exception.NetzErrorCode;


@Getter
public enum CcaErrorCode implements NetzErrorCode {

    /** Codes for Authority errors. */
    AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION("CCAAUTHORITY1001", HttpStatus.BAD_REQUEST, "User is not related to sector association", true),
    AUTHORITY_USER_IS_NOT_SECTOR_USER("CCAAUTHORITY1004", HttpStatus.BAD_REQUEST, "User is not sector user", true),
    AUTHORITY_MIN_ONE_SECTOR_ADMIN_SHOULD_EXIST("CCAAUTHORITY1002", HttpStatus.BAD_REQUEST, "At least one sector admin should exist in sector association", true),
    AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_SECTOR_USER("CCAAUTHORITY1003", HttpStatus.BAD_REQUEST, "Authority already exists for a different role type than sector user", true),
    /** Codes for Sector Association errors. */
    SECTOR_ASSOCIATION_NOT_RELATED_TO_CA("SECTORASSOC1001", HttpStatus.BAD_REQUEST, "Sector Association is not related to competent authority", true),
    SECTOR_ASSOCIATION_NO_CONTACT_FOUND("SECTORASSOC1002", HttpStatus.BAD_REQUEST, "Sector contact not found", true),
    /** Codes for Sector User errors. */
    ROLE_INVALID_SECTOR_USER_ROLE_CODE("SECTOR1001", HttpStatus.BAD_REQUEST, "Invalid sector user role code", true),
    SECTOR_USER_NOT_ACTIVE("SECTOR1002", HttpStatus.BAD_REQUEST, "Sector User is not active", true),
    /** Codes for Target Unit Account errors. */
    TARGET_UNIT_ACCOUNT_NOT_RELATED_TO_SECTOR_ASSOCIATION("TARGETUNITACC1001", HttpStatus.BAD_REQUEST, "Target Unit Account is not related to sector association", true),
    TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION("TARGETUNITACC1002", HttpStatus.BAD_REQUEST, "Target Unit Account subsector association is not related to sector association", true),
    TARGET_UNIT_ACCOUNT_RESPONSIBLE_PERSON_CONTACT_NOT_FOUND("TARGETUNITACC1003", HttpStatus.BAD_REQUEST, "Target Unit Account responsible person contact not found", true),
    TARGET_UNIT_ACCOUNT_ADMINISTRATIVE_CONTACT_NOT_FOUND("TARGETUNITACC1004", HttpStatus.BAD_REQUEST, "Target Unit Account administrative contact not found", true),
    TARGET_UNIT_ACCOUNT_ALREADY_EXISTS("TARGETUNITACC1005", HttpStatus.BAD_REQUEST, "Account already exists"),
    /** Codes for Underlying Agreement errors. */
    INVALID_UNDERLYING_AGREEMENT("UNA1001", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement"),
    INVALID_UNDERLYING_AGREEMENT_REVIEW("UNA1002", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Review"),
    INVALID_UNDERLYING_AGREEMENT_ACTIVATION("UNA1003", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Activation"),
    INVALID_UNDERLYING_AGREEMENT_TARGET_UNIT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION("UNA1004", HttpStatus.BAD_REQUEST, "Underlying Agreement Target Unit subsector association is not related to sector association", true),
    /** Codes for Admin Termination errors. */
    INVALID_ADMIN_TERMINATION("ADS1001", HttpStatus.BAD_REQUEST, "Invalid Admin Termination"),
    INVALID_ADMIN_TERMINATION_FINAL_DECISION("ADS1002", HttpStatus.BAD_REQUEST, "Invalid Admin Termination final decision"),
    INVALID_ADMIN_TERMINATION_WITHDRAW("ADS1003", HttpStatus.BAD_REQUEST, "Invalid Admin Termination withdraw"),
    /** Codes for Underlying Agreement Variation errors. */
    INVALID_UNDERLYING_AGREEMENT_VARIATION("UNAV1001", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Variation"),
    INVALID_UNDERLYING_AGREEMENT_VARIATION_REVIEW("UNAV1002", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Variation Review"),
    INVALID_UNDERLYING_AGREEMENT_VARIATION_ACTIVATION("UNAV1003", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Variation Activation");



    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
    private boolean security;

    CcaErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    CcaErrorCode(String code, HttpStatus httpStatus, String message, boolean isSecurity) {
        this(code, httpStatus, message);
        this.security = isSecurity;
    }
}
