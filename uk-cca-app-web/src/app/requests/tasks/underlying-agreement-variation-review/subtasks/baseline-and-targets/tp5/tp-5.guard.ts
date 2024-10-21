import {
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  canActivateReviewCheckYourAnswers,
  canActivateReviewSummary,
} from '@requests/common';

export const canActivateTargetPeriodCheckYourAnswers = canActivateReviewCheckYourAnswers(
  'TARGET_PERIOD5_DETAILS',
  BaseLineAndTargetsReviewStep,
);

export const canActivateTargetPeriodSummary = canActivateReviewSummary(
  'TARGET_PERIOD5_DETAILS',
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
  BaseLineAndTargetsReviewStep,
);
