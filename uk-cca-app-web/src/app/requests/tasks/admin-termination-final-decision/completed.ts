import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

export const isWizardCompleted = (reasonDetails: AdminTerminationFinalDecisionReasonDetails) =>
  !!reasonDetails?.explanation && !!reasonDetails?.finalDecisionType;
