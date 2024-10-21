import {
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  canActivateReviewCheckYourAnswers,
  canActivateReviewDecision,
  canActivateReviewSummary,
  canActivateReviewWizardStep,
} from '@requests/common';

export const canActivateTargetPeriod = canActivateReviewWizardStep(
  'TARGET_PERIOD6_DETAILS',
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
  BaseLineAndTargetsReviewStep,
);

export const canActivateTargetPeriodCheckYourAnswers = canActivateReviewCheckYourAnswers(
  'TARGET_PERIOD6_DETAILS',
  BaseLineAndTargetsReviewStep,
);

export const canActivateTargetPeriodSummary = canActivateReviewSummary(
  'TARGET_PERIOD6_DETAILS',
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
  BaseLineAndTargetsReviewStep,
);

export const canActivateTargetPeriodDecision = canActivateReviewDecision(
  'TARGET_PERIOD6_DETAILS',
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
  BaseLineAndTargetsReviewStep,
);
