import {
  canActivateReviewCheckYourAnswers,
  canActivateReviewDecision,
  canActivateReviewSummary,
  canActivateReviewWizardStep,
  FACILITIES_SUBTASK,
  FacilityWizardReviewStep,
} from '@requests/common';

export const canActivateFacility = canActivateReviewWizardStep(null, FACILITIES_SUBTASK, FacilityWizardReviewStep);

export const canActivateFacilityCheckYourAnswers = canActivateReviewCheckYourAnswers(null, FacilityWizardReviewStep);

export const canActivateFacilitySummary = canActivateReviewSummary(null, FACILITIES_SUBTASK, FacilityWizardReviewStep);

export const canActivateFacilityDecision = canActivateReviewDecision(
  null,
  FACILITIES_SUBTASK,
  FacilityWizardReviewStep,
);
