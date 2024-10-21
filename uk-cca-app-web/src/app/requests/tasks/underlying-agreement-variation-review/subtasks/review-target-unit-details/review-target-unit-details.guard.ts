import {
  canActivateReviewCheckYourAnswers,
  canActivateReviewDecision,
  canActivateReviewSummary,
  canActivateReviewWizardStep,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsReviewWizardStep,
} from '@requests/common';

export const canActivateTargetUnitDetails = canActivateReviewWizardStep(
  'TARGET_UNIT_DETAILS',
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsReviewWizardStep,
);

export const canActivateTargetUnitDetailsCheckYourAnswers = canActivateReviewCheckYourAnswers(
  'TARGET_UNIT_DETAILS',
  ReviewTargetUnitDetailsReviewWizardStep,
);

export const canActivateTargetUnitDetailsSummary = canActivateReviewSummary(
  'TARGET_UNIT_DETAILS',
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsReviewWizardStep,
);
export const canActivateTargetUnitDetailsDecision = canActivateReviewDecision(
  'TARGET_UNIT_DETAILS',
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsReviewWizardStep,
);
