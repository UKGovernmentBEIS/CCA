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
    SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION("SECTORASSOC1003", HttpStatus.BAD_REQUEST, "Subsector association is not related to sector association", true),
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
    INVALID_UNDERLYING_AGREEMENT_VARIATION_ACTIVATION("UNAV1003", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Variation Activation"),
    INVALID_UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED("UNAV1004", HttpStatus.BAD_REQUEST, "Invalid Underlying Agreement Variation Regulator Led"),
    /** Codes for Reporting Spreadsheets Download errors. */
    INVALID_PERFORMANCE_DATA_DOWNLOAD("TPRDL1001", HttpStatus.BAD_REQUEST, "Invalid Performance Data Download"),
    /** Codes for Performance Data Upload errors. */
    INVALID_PERFORMANCE_DATA_UPLOAD("TPRUL1001", HttpStatus.BAD_REQUEST, "Invalid Performance Data Upload"),
    INVALID_PERFORMANCE_DATA_UPLOAD_FILE_TYPE("TPRUL1002", HttpStatus.BAD_REQUEST, "Invalid file type"),
    UPLOAD_ZIP_FILE_CONTAINS_INVALID_FILE_TYPES("TPRUL1003", HttpStatus.BAD_REQUEST, "Zip file contains invalid file types"),
    INVALID_PERFORMANCE_DATA_UPDATE_ACCOUNT_LOCKED("TPRUL1004", HttpStatus.BAD_REQUEST, "Account is locked for target period reporting"),
    INVALID_PERFORMANCE_DATA_UPDATE_INVALID_REPORT_VERSION("TPRUL1005", HttpStatus.BAD_REQUEST, "Performance data report version is invalid"),
    /** Codes for Performance Account Template Data errors */
    PERFORMANCE_ACCOUNT_TEMPLATE_NOT_COMPLETED_YET("PATUL0001", HttpStatus.BAD_REQUEST, "Processing has not been complete yet"),
    INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_FILE_TYPE("PATUL1002", HttpStatus.BAD_REQUEST, "Invalid file type"),
    /** Codes for Subsistence Fee Run errors. */
    SECTOR_MOA_CONTAINS_NO_FACILITIES("SFR1001", HttpStatus.BAD_REQUEST, "Sector MoA contains no facilities"),
    TARGET_UNIT_MOA_CONTAINS_NO_FACILITIES("SFR1002", HttpStatus.BAD_REQUEST, "Target Unit MoA contains no facilities"),
    FILE_EVIDENCE_IS_NOT_RELATED_TO_SUBSISTENCE_FEES_MOA("SFR1003", HttpStatus.BAD_REQUEST, "File evidence is not related to the provided subsistence fees moa"),
    NEGATIVE_SUBSISTENCE_FEES_MOA_RECEIVED_AMOUNT("SFR1004", HttpStatus.BAD_REQUEST,"Negative subsistence fees moa received amount is not permitted"),
    ZERO_SUBSISTENCE_FEES_MOA_TRANSACTION_AMOUNT("SFR1006", HttpStatus.BAD_REQUEST,"Zero transaction amount is not allowed for updating subsistence fees moa received amount"),
    SUBMITTER_HAS_NO_ACCESS_TO_FILE_EVIDENCE("SFR1007", HttpStatus.BAD_REQUEST,"Submitter is not allowed to access the file evidence"),
    SUBSISTENCE_FEES_MOA_TARGET_UNIT_ID_DOES_NOT_EXIST("SFR1008", HttpStatus.BAD_REQUEST,"Some moa target unit ids do not exist in moa"),
    SUBSISTENCE_FEES_MOA_FACILITY_ID_DOES_NOT_EXIST("SFR1009", HttpStatus.BAD_REQUEST,"Some moa facility ids do not exist in moa target unit"),
    /** Codes for Buy Out Surplus Transaction errors. */
    INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS("BS1001", HttpStatus.BAD_REQUEST, "Invalid payment status"),
    TERMINATED_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS("BS1002", HttpStatus.BAD_REQUEST, "Changes are not allowed because the transaction has been automatically marked as terminated"),
    FILE_EVIDENCE_IS_NOT_RELATED_TO_TRANSACTION("BS1003", HttpStatus.BAD_REQUEST, "File evidence is not related to the provided transaction"),
    /** Codes for Facility Certification errors. */
    INVALID_PROVIDED_ACCOUNTS("CRT1001", HttpStatus.BAD_REQUEST, "Invalid provided accounts"),
    NO_FACILITIES_FOR_ACCOUNT("CRT1002", HttpStatus.BAD_REQUEST, "No facilities found for account"),
    FACILITY_CERTIFICATION_RUN_EXIST("CRT1003", HttpStatus.BAD_REQUEST, "Facility certification run already in progress"),
    FACILITY_CERTIFICATION_START_DATE_OUTSIDE_PERIOD("CRT1004", HttpStatus.BAD_REQUEST, "Invalid provided certified start date"),
    CERT_STATUS_UPDATE_BEFORE_CERT_PERIOD_START_ERROR("CRT1005", HttpStatus.BAD_REQUEST, "Cannot update the certification status before certification period starting date"),
    /** Code for Peer Review errors */
    PEER_REVIEW_ATTACHMENT_NOT_FOUND("PRV1001", HttpStatus.BAD_REQUEST, "Peer review attachment not found"),
    /** Codes for CCA3 Existing Facilities Migration errors. */
    CCA3_EXISTING_FACILITIES_MIGRATION_RUN_EXIST("CCA3EFM1001", HttpStatus.BAD_REQUEST, "CCA3 Existing Facilities Migration run already in progress"),
    CCA3_EXISTING_FACILITIES_MIGRATION_RUN_CSV_FAILED("CCA3EFM1002", HttpStatus.BAD_REQUEST, "CCA3 Existing Facilities Migration run csv parsing failed"),
    INVALID_CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION("CCA3EFM1003", HttpStatus.BAD_REQUEST, "Invalid Activation"),
    /** Facility Audit */
    INVALID_FACILITY_AUDIT("AUDT1001", HttpStatus.BAD_REQUEST, "Invalid Facility Audit"),
    /** Codes for Cca2 extension notice errors. */
    CCA2_EXTENSION_NOTICE_RUN_EXIST("CCA2EXT1001", HttpStatus.BAD_REQUEST, "Cca2 extension notice run already in progress"),
    /** Codes for CCA2 termination run errors. */
    CCA2_TERMINATION_RUN_EXIST("CCA2END1001", HttpStatus.BAD_REQUEST, "CCA2 termination run already in progress"),
    /** Non Compliance */
    INVALID_NON_COMPLIANCE("NCOM1001", HttpStatus.BAD_REQUEST, "Invalid Non Compliance")
    ;


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
