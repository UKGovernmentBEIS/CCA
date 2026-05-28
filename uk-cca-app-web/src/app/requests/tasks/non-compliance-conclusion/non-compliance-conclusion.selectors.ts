import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  NonComplianceConclusion,
  NonComplianceConclusionDetails,
  NonComplianceConclusionSubmitRequestTaskPayload,
  NonComplianceWithdrawNotice,
} from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, NonComplianceConclusionSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as NonComplianceConclusionSubmitRequestTaskPayload,
  );

const selectNonComplianceConclusion: StateSelector<RequestTaskState, NonComplianceConclusion> =
  createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceConclusion);

const selectConclusionDetails: StateSelector<RequestTaskState, NonComplianceConclusionDetails> =
  createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceConclusion?.details);

const selectWithdrawNotice: StateSelector<RequestTaskState, NonComplianceWithdrawNotice> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.nonComplianceConclusion?.withdrawNotice,
);

const selectAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.nonComplianceAttachments,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

const selectIsPenaltyReissue: StateSelector<RequestTaskState, boolean> = createDescendingSelector(
  selectConclusionDetails,
  (details) => details?.penaltyOutcome === 'REISSUE',
);

export const nonComplianceConclusionQuery = {
  selectPayload,
  selectNonComplianceConclusion,
  selectConclusionDetails,
  selectWithdrawNotice,
  selectAttachments,
  selectSectionsCompleted,
  selectIsPenaltyReissue,
};
