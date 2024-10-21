import {
  canActivateReviewCheckYourAnswers,
  canActivateReviewDecision,
  canActivateReviewSummary,
  canActivateReviewWizardStep,
  VARIATION_DETAILS_SUBTASK,
  VariationDetailsReviewWizardStep,
} from '@requests/common';

export const canActivateVariationDetails = canActivateReviewWizardStep(
  'VARIATION_DETAILS',
  VARIATION_DETAILS_SUBTASK,
  VariationDetailsReviewWizardStep,
);

export const canActivateVariationDetailsCheckYourAnswers = canActivateReviewCheckYourAnswers(
  'VARIATION_DETAILS',
  VariationDetailsReviewWizardStep,
);
export const canActivateVariationDetailsSummary = canActivateReviewSummary(
  'VARIATION_DETAILS',
  VARIATION_DETAILS_SUBTASK,
  VariationDetailsReviewWizardStep,
);

export const canActivateVariationDetailsDecision = canActivateReviewDecision(
  'VARIATION_DETAILS',
  VARIATION_DETAILS_SUBTASK,
  VariationDetailsReviewWizardStep,
);
