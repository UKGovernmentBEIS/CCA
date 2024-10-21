import { Determination } from 'cca-api';

export const isWizardCompleted = (determination: Determination) => {
  if (!determination) return false;
  if (determination.type === 'ACCEPTED') return true;
  if (determination.reason) return true;
  return false;
};
