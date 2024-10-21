import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceReviewWizardStep,
  canActivateReviewCheckYourAnswers,
  canActivateReviewDecision,
  canActivateReviewSummary,
  canActivateReviewWizardStep,
} from '@requests/common';

export const canActivateAuthorisationAndAdditionalEvidence = canActivateReviewWizardStep(
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceReviewWizardStep,
);

export const canActivateAuthorisationAdditionalEvidenceCheckYourAnswers = canActivateReviewCheckYourAnswers(
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
  AuthorisationAdditionalEvidenceReviewWizardStep,
);
export const canActivateAuthorisationAdditionalEvidenceSummary = canActivateReviewSummary(
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceReviewWizardStep,
);

export const canActivateAuthorisationAdditionalEvidenceDecision = canActivateReviewDecision(
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceReviewWizardStep,
);
