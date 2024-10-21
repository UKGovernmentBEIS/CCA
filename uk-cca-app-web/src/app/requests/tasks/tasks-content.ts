import { RequestTaskPageContentFactoryMap } from '@netz/common/request-task';

import { adminTerminationTaskContent } from './admin-termination/admin-termination-task-content';
import { adminTerminationFinalDecisionTaskContent } from './admin-termination-final-decision/admin-termination-final-decision-task-content';
import { underlyingAgreementActivationTaskContent } from './underlying-agreement-activation/underlying-agreement-activation-task-content';
import { underlyingAgreementApplicationTaskContent } from './underlying-agreement-application/underlying-agreement-application-task-content';
import { underlyingAgreementReviewTaskContent } from './underlying-agreement-review/underlying-agreement-review-task-content';
import { underlyingAgreementVariationTaskContent } from './underlying-agreement-variation/underlying-agreement-variation-task-content';
import { underlyingAgreementVariationReviewTaskContent } from './underlying-agreement-variation-review/underlying-agreement-variation-review-task-content';
import { underlyingAgreementVariationWaitActivationTaskContent } from './underlying-agreement-variation-wait-activation/underlying-agreement-variation-wait-activation-task-content';
import { underlyingAgreementVariationWaitReviewTaskContent } from './underlying-agreement-variation-wait-review/underlying-agreement-variation-wait-review-task-content';
import { underlyingAgreementWaitActivationTaskContent } from './underlying-agreement-wait-activation/underlying-agreement-wait-activation-task-content';
import { underlyingAgreementWaitReviewTaskContent } from './underlying-agreement-wait-review/underlying-agreement-wait-review-task-content';
import { withdrawAdminTerminationTaskContent } from './withdraw-admin-termination/withdraw-admin-termination-task-content';

export const tasksContent: RequestTaskPageContentFactoryMap = {
  UNDERLYING_AGREEMENT_APPLICATION_SUBMIT: underlyingAgreementApplicationTaskContent,
  UNDERLYING_AGREEMENT_APPLICATION_REVIEW: underlyingAgreementReviewTaskContent,
  UNDERLYING_AGREEMENT_WAIT_FOR_REVIEW: underlyingAgreementWaitReviewTaskContent,
  UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION: underlyingAgreementActivationTaskContent,
  UNDERLYING_AGREEMENT_WAIT_FOR_ACTIVATION: underlyingAgreementWaitActivationTaskContent,

  ADMIN_TERMINATION_APPLICATION_SUBMIT: adminTerminationTaskContent,
  ADMIN_TERMINATION_APPLICATION_WITHDRAW: withdrawAdminTerminationTaskContent,
  ADMIN_TERMINATION_APPLICATION_FINAL_DECISION: adminTerminationFinalDecisionTaskContent,

  UNDERLYING_AGREEMENT_VARIATION_SUBMIT: underlyingAgreementVariationTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW: underlyingAgreementVariationReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_REVIEW: underlyingAgreementVariationWaitReviewTaskContent,
  UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_ACTIVATION: underlyingAgreementVariationWaitActivationTaskContent,
};
