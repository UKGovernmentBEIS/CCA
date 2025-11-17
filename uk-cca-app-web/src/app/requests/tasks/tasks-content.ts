import { RequestTaskPageContentFactoryMap } from '@netz/common/request-task';

import { adminTerminationTaskContent } from './admin-termination/admin-termination-task-content';
import { adminTerminationFinalDecisionTaskContent } from './admin-termination-final-decision/admin-termination-final-decision-task-content';
import { adminTerminationPeerReviewTaskContent } from './admin-termination-peer-review/admin-termination-peer-review-task-content';
import { adminTerminationWaitForPeerReviewTaskContent } from './admin-termination-wait-for-peer-review/admin-termination-wait-for-peer-review-task-content';
import { cca3MigrationAccountActivationTaskContent } from './cca3-migration-account-activation/cca3-migration-account-activation-task-content';
import { patUploadTaskContent } from './performance-account-template-upload/pat-upload-task-content';
import { performanceDataDownloadTaskContent } from './performance-data-download/performance-data-download-task-content';
import { performanceDataUploadTaskContent } from './performance-data-upload/performance-data-upload-task-content';
import { preAuditReviewTaskContent } from './pre-audit-review/pre-audit-review-task-content';
import { underlyingAgreementActivationTaskContent } from './underlying-agreement-activation/underlying-agreement-activation-task-content';
import { underlyingAgreementApplicationTaskContent } from './underlying-agreement-application/underlying-agreement-application-task-content';
import { underlyingAgreementPeerReviewTaskContent } from './underlying-agreement-peer-review/underlying-agreement-peer-review-task-content';
import { underlyingAgreementReviewTaskContent } from './underlying-agreement-review/underlying-agreement-review-task-content';
import { underlyingAgreementVariationTaskContent } from './underlying-agreement-variation/underlying-agreement-variation-task-content';
import { underlyingAgreementVariationActivationTaskContent } from './underlying-agreement-variation-activation/underlying-agreement-variation-activation-task-content';
import { underlyingAgreementVariationPeerReviewTaskContent } from './underlying-agreement-variation-peer-review/underlying-agreement-variation-peer-review-task-content';
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

  PERFORMANCE_DATA_DOWNLOAD_SUBMIT: performanceDataDownloadTaskContent,
  PERFORMANCE_DATA_UPLOAD_SUBMIT: performanceDataUploadTaskContent,
  PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT: patUploadTaskContent,

  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION: cca3MigrationAccountActivationTaskContent,

  PRE_AUDIT_REVIEW_SUBMIT: preAuditReviewTaskContent,
};
