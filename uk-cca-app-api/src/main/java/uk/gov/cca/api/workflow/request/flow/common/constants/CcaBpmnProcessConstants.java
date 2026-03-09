package uk.gov.cca.api.workflow.request.flow.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Encapsulates domain related to BPMN Process for CCA workflows
 */
@UtilityClass
public class CcaBpmnProcessConstants {

    public static final String EXPIRATION_DATE = "ExpirationDate";
    public static final String ACCOUNT_IDS = "accountIds";
    public static final String SECTOR_IDS = "sectorIds";
    public static final String SECTOR_ID = "sectorId";
    public static final String NUMBER_OF_ACCOUNTS_COMPLETED = "accountsCompleted";
    public static final String NUMBER_OF_SECTORS_COMPLETED = "sectorsCompleted";

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

    // Performance data
    public static final String PERFORMANCE_DATA_ACCOUNT_REPORT = "accountReport";
    public static final String PERFORMANCE_DATA_ERROR_MESSAGE = "errorMessage";

    public static final String PERFORMANCE_DATA_DOWNLOAD_REQUEST_BUSINESS_KEY = "performanceDataDownloadRequestBusinessKey";
    public static final String PERFORMANCE_DATA_DOWNLOAD_ZIP_FILE = "zipFile";
    public static final String PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE = "errorsFile";
    public static final String PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED = "accountsReportGenerateCompleted";
    public static final String PERFORMANCE_DATA_GENERATE_REQUEST_BUSINESS_KEY = "performanceDataGenerateRequestBusinessKey";

    public final String PERFORMANCE_DATA_UPLOAD_REQUEST_BUSINESS_KEY = "reportingSpreadsheetsUploadRequestBusinessKey";
    public final String PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS = "accountReports";
    public final String PERFORMANCE_DATA_UPLOAD_NUMBER_OF_ACCOUNTS_COMPLETED = "accountsReportProcessingCompleted";
    public final String PERFORMANCE_DATA_UPLOAD_PROCESSING_NUMBER_OF_ACCOUNTS_COMPLETED = "accountsReportProcessingCompleted";
    public final String PERFORMANCE_DATA_PROCESSING_REQUEST_BUSINESS_KEY = "performanceDataProcessingRequestBusinessKey";


    // PAT
    public final String PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT = PERFORMANCE_DATA_ACCOUNT_REPORT;
    public final String PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS = "accountReports";
    public final String PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_REQUEST_BUSINESS_KEY = "performanceAccountTemplateDataUploadRequestBusinessKey";
    public final String PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING_REQUEST_BUSINESS_KEY = "performanceAccountTemplateDataProcessingRequestBusinessKey";


    // Subsistence fees
    public static final String SUBSISTENCE_FEES_REQUEST_BUSINESS_KEY = "subsistenceFeesRequestBusinessKey";
    public static final String SECTOR_MOA_REQUEST_ID = "sectorMoaRequestId";
    public static final String SECTOR_MOA_REQUEST_SUCCEEDED = "sectorMoaRequestSucceeded";
    public static final String SECTOR_MOA_REQUEST_ERRORS = "sectorMoaRequestErrors";
    public static final String TARGET_UNIT_MOA_REQUEST_ID = "targetUnitMoaRequestId";
    public static final String TARGET_UNIT_MOA_REQUEST_SUCCEEDED = "targetUnitMoaRequestSucceeded";
    public static final String TARGET_UNIT_MOA_REQUEST_ERRORS = "targetUnitMoaRequestErrors";

    // Buy Out Surplus
    public final String BUY_OUT_SURPLUS_ACCOUNT_STATE = "buyOutSurplusAccountState";
    public final String BUY_OUT_SURPLUS_RUN_REQUEST_BUSINESS_KEY = "buyOutSurplusRunRequestBusinessKey";

    // Facility Certification
    public final String FACILITY_CERTIFICATION_RUN_INITIATE_FLAG = "facilityCertificationRunInitiateFlag";
    public final String FACILITY_CERTIFICATION_PERIOD = "facilityCertificationPeriod";
    public final String FACILITY_CERTIFICATION_ACCOUNT_STATE = "facilityCertificationAccountState";
    public final String FACILITY_CERTIFICATION_RUN_REQUEST_BUSINESS_KEY = "facilityCertificationRunRequestBusinessKey";

    // Facility Audit
    public static final String FACILITY_AUDIT_OUTCOME = "facilityAuditOutcome";
    public static final String IS_FURTHER_AUDIT_NEEDED = "isFurtherAuditNeeded";
    public static final String IS_CORRECTIVE_ACTIONS_NEEDED = "isCorrectiveActionsNeeded";
    public static final String TIMER_RECALCULATED = "timerRecalculated";
    public static final String FACILITY_AUDIT_EXPIRATION_DATE = CcaRequestExpirationKey.FACILITY_AUDIT + EXPIRATION_DATE;

    // CCA3 Existing Facilities Migration
    public final String CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE = "cca3ExistingFacilitiesMigrationAccountState";
    public final String CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_BUSINESS_KEY = "cca3ExistingFacilitiesMigrationRunRequestBusinessKey";
    public final String CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_AGREEMENT_OUTCOME = "cca3ExistingFacilitiesMigrationAccountProcessingAgreementOutcome";

    // CCA2 Extension Notice
    public final String CCA2_EXTENSION_NOTICE_ACCOUNT_STATE = "cca2ExtensionNoticeAccountState";
    public final String CCA2_EXTENSION_NOTICE_RUN_REQUEST_BUSINESS_KEY = "cca2ExtensionNoticeRunRequestBusinessKey";

    // CCA2 Termination
    public final String CCA2_TERMINATION_RUN_INITIATE_FLAG = "cca2TerminationRunInitiateFlag";
    public final String CCA2_TERMINATION_ACCOUNT_STATE = "cca2TerminationAccountState";
    public final String CCA2_TERMINATION_RUN_REQUEST_BUSINESS_KEY = "cca2TerminationRunRequestBusinessKey";

    // Non Compliance
    public static final String NON_COMPLIANCE_OUTCOME = "nonComplianceOutcome";
    public static final String IS_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NEEDED = "isNonComplianceEnforcementResponseNoticeNeeded";
    public static final String IS_NON_COMPLIANCE_PENALTY_NOTICE_NEEDED = "isNonCompliancePenaltyNoticeNeeded";
    public static final String IS_NON_COMPLIANCE_REISSUE_PENALTY_NEEDED = "isNonComplianceReissuePenaltyNeeded";
}
