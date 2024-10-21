import { UuidFilePair } from '@shared/components';

import {
  AdminTerminationWithdrawReasonDetails,
  AdminTerminationWithdrawRequestTaskPayload,
  AdminTerminationWithdrawSaveRequestTaskActionPayload,
} from 'cca-api';

export type WADTRequestTaskPayload = AdminTerminationWithdrawRequestTaskPayload;

export type WADTSaveRequestTaskPayload = AdminTerminationWithdrawSaveRequestTaskActionPayload;

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
