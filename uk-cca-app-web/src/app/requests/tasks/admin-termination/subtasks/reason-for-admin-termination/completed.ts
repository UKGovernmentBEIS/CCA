import { AdminTerminationReasonDetails } from 'cca-api';

export const isWizardCompleted = (adminTerminationReasonDetails: AdminTerminationReasonDetails) => {
  return !!adminTerminationReasonDetails.reason && !!adminTerminationReasonDetails.explanation;
};
