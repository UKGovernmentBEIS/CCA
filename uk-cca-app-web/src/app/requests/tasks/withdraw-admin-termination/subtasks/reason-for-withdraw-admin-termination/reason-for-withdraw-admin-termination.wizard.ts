import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

export const isWizardCompleted = (adminTerminationWithdrawReasonDetails: AdminTerminationWithdrawReasonDetails) => {
  return !!adminTerminationWithdrawReasonDetails.explanation;
};
