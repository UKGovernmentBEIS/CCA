import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { NonComplianceDetails, NonComplianceDetailsSubmitRequestTaskPayload, WorkflowFacilityDTO } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, NonComplianceDetailsSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as NonComplianceDetailsSubmitRequestTaskPayload,
  );

const selectNonComplianceDetails: StateSelector<RequestTaskState, NonComplianceDetails> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.nonComplianceDetails,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

const selectAllRelevantWorkflows: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.allRelevantWorkflows,
);

const selectRelevantWorkflows: StateSelector<RequestTaskState, string[]> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.nonComplianceDetails?.relevantWorkflows,
);

const selectAllRelevantFacilities: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.allRelevantFacilities,
);

const selectRelevantFacilities: StateSelector<RequestTaskState, WorkflowFacilityDTO[]> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.nonComplianceDetails?.relevantFacilities,
);

export const nonComplianceDetailsQuery = {
  selectPayload,
  selectNonComplianceDetails,
  selectSectionsCompleted,
  selectAllRelevantWorkflows,
  selectRelevantWorkflows,
  selectAllRelevantFacilities,
  selectRelevantFacilities,
};
