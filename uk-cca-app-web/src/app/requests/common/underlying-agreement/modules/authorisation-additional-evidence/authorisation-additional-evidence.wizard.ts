import { AuthorisationAndAdditionalEvidence } from 'cca-api';

export const isAdditionalEvidenceWizardCompleted = (
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
) => {
  return !!authorisationAndAdditionalEvidence.authorisationAttachmentIds;
};
