import { UuidFilePair } from '@shared/components';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

export const ADMIN_TERMINATION_FINAL_DECISION_SUBTASK = 'adminTerminationFinalDecisionReasonDetails';

export enum AdminTerminationFinalDecisionTerminateAgreementWizardStep {
  ACTIONS = 'actions',
  REASON_DETAILS = 'reason-details',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export type AdminTerminationFinalDecisionReasonDetailsUserInput = Omit<
  AdminTerminationFinalDecisionReasonDetails,
  'relevantFiles'
> & {
  relevantFiles: UuidFilePair[];
};
