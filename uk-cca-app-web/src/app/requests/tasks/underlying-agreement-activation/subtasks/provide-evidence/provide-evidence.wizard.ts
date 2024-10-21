import { UnderlyingAgreementActivationDetails } from 'cca-api';

export const isWizardCompleted = (details: UnderlyingAgreementActivationDetails) => {
  return !!details;
};
