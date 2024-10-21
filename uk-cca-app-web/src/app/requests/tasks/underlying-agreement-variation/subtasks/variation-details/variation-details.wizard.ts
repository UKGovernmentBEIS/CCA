import { UnderlyingAgreementVariationDetails } from 'cca-api';

export const isWizardCompleted = (variationDetails: UnderlyingAgreementVariationDetails) => {
  return !!variationDetails;
};
