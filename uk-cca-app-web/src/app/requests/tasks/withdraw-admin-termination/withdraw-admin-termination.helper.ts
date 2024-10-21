import { UuidFilePair } from '@shared/components';

import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

export const REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK = 'adminTerminationWithdrawReasonDetails';

export enum ReasonForWithdrawAdminTerminationWizardStep {
  REASON_DETAILS = 'reason-details',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export type AdminTerminationWithdrawReasonDetailsUserInput = Omit<
  AdminTerminationWithdrawReasonDetails,
  'relevantFiles'
> & {
  relevantFiles: UuidFilePair[];
};
