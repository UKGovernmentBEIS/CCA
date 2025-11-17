import {
  CcaDecisionNotification,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

import { CCA3MigrationRequestTaskPayload, CCA3MigrationSaveRequestTaskActionPayload } from './types';

export function createSaveActionDTO(
  requestTaskId: number,
  updatedPayload: CCA3MigrationRequestTaskPayload,
  sectionsCompleted: Record<string, string>,
): RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CCA3MigrationSaveRequestTaskActionPayload;
} {
  return {
    requestTaskId,
    requestTaskActionType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_SAVE_PAYLOAD',
      activationDetails: updatedPayload.activationDetails,
      sectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
} {
  return {
    requestTaskId,
    requestTaskActionType:
      'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
