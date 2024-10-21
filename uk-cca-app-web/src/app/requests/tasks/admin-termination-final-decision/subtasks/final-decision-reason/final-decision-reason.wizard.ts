import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

export const isWizardCompleted = (
  adminTerminationFinalDecisionReasonDetails: AdminTerminationFinalDecisionReasonDetails,
) => {
  return (
    !!adminTerminationFinalDecisionReasonDetails.explanation &&
    !!adminTerminationFinalDecisionReasonDetails.finalDecisionType
  );
};
