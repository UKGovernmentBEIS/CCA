import { RequestTaskPageContentFactoryMap } from '@netz/common/request-task';

import { adminTerminationTaskContent } from './admin-termination/admin-termination-task-content';
import { adminTerminationFinalDecisionTaskContent } from './admin-termination-final-decision/admin-termination-final-decision-task-content';
import { adminTerminationPeerReviewTaskContent } from './admin-termination-peer-review/admin-termination-peer-review-task-content';
import { adminTerminationWaitForPeerReviewTaskContent } from './admin-termination-wait-for-peer-review/admin-termination-wait-for-peer-review-task-content';
import { appealOutcomeTaskContent } from './appeal-outcome/appeal-outcome-task-content';
import { auditDetailsCorrectiveActionsTaskContent } from './audit-details-corrective-actions/audit-details-corrective-actions-task-content';
import { cca3MigrationAccountActivationTaskContent } from './cca3-migration-account-activation/cca3-migration-account-activation-task-content';
import { enforcementResponseNoticeTaskContent } from './enforcement-response-notice/enforcement-response-notice-task-content';
import { enforcementResponseNoticePeerReviewTaskContent } from './enforcement-response-notice-peer-review/enforcement-response-notice-peer-review-task-content';
import { enforcementResponseNoticeWaitForPeerReviewTaskContent } from './enforcement-response-notice-wait-for-peer-review/enforcement-response-notice-wait-for-peer-review-task-content';
import { nonComplianceConclusionTaskContent } from './non-compliance-conclusion/non-compliance-conclusion-task-content';
import { nonComplianceDetailsTaskContent } from './non-compliance-details/non-compliance-details-task-content';
import { noticeOfIntentTaskContent } from './notice-of-intent/notice-of-intent-task-content';
import { noticeOfIntentPeerReviewTaskContent } from './notice-of-intent-peer-review/notice-of-intent-peer-review-task-content';
import { noticeOfIntentWaitForPeerReviewTaskContent } from './notice-of-intent-wait-for-peer-review/notice-of-intent-wait-for-peer-review-task-content';
import { patUploadTaskContent } from './performance-account-template-upload/pat-upload-task-content';
import { performanceDataDownloadTaskContent } from './performance-data-download/performance-data-download-task-content';
import { performanceDataUploadTaskContent } from './performance-data-upload/performance-data-upload-task-content';
import { preAuditReviewTaskContent } from './pre-audit-review/pre-audit-review-task-content';
import { tprCSVUploadTaskContent } from './target-period-reporting-csv-upload/target-period-reporting-csv-upload-task-content';
import { targetPeriodReportingFormTaskContent } from './target-period-reporting-form/target-period-reporting-form-task-content';
import { trackCorrectiveActionsTaskContent } from './track-corrective-actions/track-corrective-actions-task-content';
import { underlyingAgreementActivationTaskContent } from './underlying-agreement-activation/underlying-agreement-activation-task-content';
import { underlyingAgreementApplicationTaskContent } from './underlying-agreement-application/underlying-agreement-application-task-content';
import { underlyingAgreementPeerReviewTaskContent } from './underlying-agreement-peer-review/underlying-agreement-peer-review-task-content';
import { underlyingAgreementReviewTaskContent } from './underlying-agreement-review/underlying-agreement-review-task-content';
import { underlyingAgreementVariationTaskContent } from './underlying-agreement-variation/underlying-agreement-variation-task-content';
import { underlyingAgreementVariationActivationTaskContent } from './underlying-agreement-variation-activation/underlying-agreement-variation-activation-task-content';
import { underlyingAgreementVariationPeerReviewTaskContent } from './underlying-agreement-variation-peer-review/underlying-agreement-variation-peer-review-task-content';
import { underlyingAgreementVariationRegulatorLedTaskContent } from './underlying-agreement-variation-regulator-led/underlying-agreement-variation-regulator-led-task-content';
import { unaRegulatorLedVariationPeerReviewTaskContent } from './underlying-agreement-variation-regulator-led-peer-review/underlying-agreement-variation-regulator-led-peer-review-task-content';
import { unaRegulatorLedVariationWaitForPeerReviewTaskContent } from './underlying-agreement-variation-regulator-led-wait-for-peer-review/underlying-agreement-variation-regulator-led-wait-for-peer-review-task-content';
import { underlyingAgreementVariationReviewTaskContent } from './underlying-agreement-variation-review/underlying-agreement-variation-review-task-content';
import { underlyingAgreementVariationWaitActivationTaskContent } from './underlying-agreement-variation-wait-activation/underlying-agreement-variation-wait-activation-task-content';
import { underlyingAgreementVariationWaitForPeerReviewTaskContent } from './underlying-agreement-variation-wait-for-peer-review/underlying-agreement-variation-wait-for-peer-review-task-content';
import { underlyingAgreementVariationWaitReviewTaskContent } from './underlying-agreement-variation-wait-review/underlying-agreement-variation-wait-review-task-content';
import { underlyingAgreementWaitActivationTaskContent } from './underlying-agreement-wait-activation/underlying-agreement-wait-activation-task-content';
import { underlyingAgreementWaitForPeerReviewTaskContent } from './underlying-agreement-wait-for-peer-review/underlying-agreement-wait-for-peer-review-task-content';
import { underlyingAgreementWaitReviewTaskContent } from './underlying-agreement-wait-review/underlying-agreement-wait-review-task-content';
import { withdrawAdminTerminationTaskContent } from './withdraw-admin-termination/withdraw-admin-termination-task-content';

export const tasksContent: RequestTaskPageContentFactoryMap = {
  UNDERLYING_AGREEMENT_APPLICATION_SUBMIT: underlyingAgreementApplicationTaskContent,
  UNDERLYING_AGREEMENT_APPLICATION_REVIEW: underlyingAgreementReviewTaskContent,
  UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW: underlyingAgreementPeerReviewTaskContent,
  UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW: underlyingAgreementWaitForPeerReviewTaskContent,
  UNDERLYING_AGREEMENT_WAIT_FOR_REVIEW: underlyingAgreementWaitReviewTaskContent,
  UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION: underlyingAgreementActivationTaskContent,
  UNDERLYING_AGREEMENT_WAIT_FOR_ACTIVATION: underlyingAgreementWaitActivationTaskContent,

  ADMIN_TERMINATION_APPLICATION_SUBMIT: adminTerminationTaskContent,
  ADMIN_TERMINATION_APPLICATION_WITHDRAW: withdrawAdminTerminationTaskContent,
  ADMIN_TERMINATION_APPLICATION_FINAL_DECISION: adminTerminationFinalDecisionTaskContent,
  ADMIN_TERMINATION_APPLICATION_PEER_REVIEW: adminTerminationPeerReviewTaskContent,
  ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW: adminTerminationWaitForPeerReviewTaskContent,

  UNDERLYING_AGREEMENT_VARIATION_SUBMIT: underlyingAgreementVariationTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW: underlyingAgreementVariationReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW: underlyingAgreementVariationPeerReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW: underlyingAgreementVariationWaitForPeerReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_REVIEW: underlyingAgreementVariationWaitReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_ACTIVATION: underlyingAgreementVariationWaitActivationTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_ACTIVATION: underlyingAgreementVariationActivationTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT: underlyingAgreementVariationRegulatorLedTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW:
    unaRegulatorLedVariationWaitForPeerReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW: unaRegulatorLedVariationPeerReviewTaskContent,

  PERFORMANCE_DATA_DOWNLOAD_SUBMIT: performanceDataDownloadTaskContent,
  PERFORMANCE_DATA_UPLOAD_SUBMIT: performanceDataUploadTaskContent,
  PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT: patUploadTaskContent,
  PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT: tprCSVUploadTaskContent,

  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION: cca3MigrationAccountActivationTaskContent,

  PRE_AUDIT_REVIEW_SUBMIT: preAuditReviewTaskContent,
  AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT: auditDetailsCorrectiveActionsTaskContent,
  AUDIT_TRACK_CORRECTIVE_ACTIONS: trackCorrectiveActionsTaskContent,

  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_ACTIVATION: underlyingAgreementVariationActivationTaskContent,
  NON_COMPLIANCE_DETAILS_SUBMIT: nonComplianceDetailsTaskContent,
  NON_COMPLIANCE_CONCLUSION_SUBMIT: nonComplianceConclusionTaskContent,
  NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT: appealOutcomeTaskContent,
  NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT: noticeOfIntentTaskContent,
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT: enforcementResponseNoticeTaskContent,
  NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW: noticeOfIntentPeerReviewTaskContent,
  NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW: noticeOfIntentWaitForPeerReviewTaskContent,
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW: enforcementResponseNoticePeerReviewTaskContent,
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_WAIT_FOR_PEER_REVIEW:
    enforcementResponseNoticeWaitForPeerReviewTaskContent,

  PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT: targetPeriodReportingFormTaskContent,
};
