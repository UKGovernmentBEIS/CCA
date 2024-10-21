import { UuidFilePair } from '@shared/components';

import {
  AdminTerminationReasonDetails,
  AdminTerminationSaveRequestTaskActionPayload,
  AdminTerminationSubmitRequestTaskPayload,
} from 'cca-api';

export const REASON_FOR_ADMIN_TERMINATION_SUBTASK = 'adminTerminationReasonDetails';

export enum ReasonForAdminTerminationWizardStep {
  REASON_DETAILS = 'reason-details',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export type ADTRequestTaskPayload = AdminTerminationSubmitRequestTaskPayload;
export type ADTSaveRequestTaskPayload = AdminTerminationSaveRequestTaskActionPayload;

export type AdminTerminationReasonDetailsUserInput = Omit<AdminTerminationReasonDetails, 'relevantFiles'> & {
  relevantFiles: UuidFilePair[];
};
