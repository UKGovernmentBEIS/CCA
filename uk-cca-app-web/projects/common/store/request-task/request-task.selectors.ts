import {
  ItemDTO,
  RequestActionInfoDTO,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'cca-api';

import { createAggregateSelector, createDescendingSelector, createSelector, StateSelector } from '../index';
import { RequestTaskState } from './request-task.state';

type RequestTaskActions = RequestTaskItemDTO['allowedRequestTaskActions'];

const selectRequestTaskItem: StateSelector<RequestTaskState, RequestTaskItemDTO | null> = createSelector(
  (state) => state?.requestTaskItem,
);

const selectAllowedRequestTaskActions: StateSelector<
  RequestTaskState,
  RequestTaskItemDTO['allowedRequestTaskActions'] | undefined
> = createDescendingSelector(selectRequestTaskItem, (state) => state?.allowedRequestTaskActions);

const selectRelatedActions: StateSelector<RequestTaskState, RequestTaskActions | undefined> = createDescendingSelector(
  selectRequestTaskItem,
  (state) => state?.allowedRequestTaskActions,
);

const selectRequestInfo: StateSelector<RequestTaskState, RequestInfoDTO | undefined> = createDescendingSelector(
  selectRequestTaskItem,
  (state) => state?.requestInfo,
);

const selectRequestId: StateSelector<RequestTaskState, string | undefined> = createDescendingSelector(
  selectRequestInfo,
  (state) => state?.id,
);

const selectRequestType: StateSelector<RequestTaskState, RequestInfoDTO['type'] | undefined> = createDescendingSelector(
  selectRequestInfo,
  (state) => state?.type,
);

const selectRequestMetadata: StateSelector<RequestTaskState, RequestInfoDTO['requestMetadata'] | undefined> =
  createDescendingSelector(selectRequestInfo, (state) => state?.requestMetadata);

const selectRequestTask: StateSelector<RequestTaskState, RequestTaskDTO | undefined> = createDescendingSelector(
  selectRequestTaskItem,
  (state) => state?.requestTask,
);

const selectRequestTaskId: StateSelector<RequestTaskState, number | undefined> = createDescendingSelector(
  selectRequestTask,
  (state) => state?.id,
);

const selectUserAssignCapable: StateSelector<RequestTaskState, boolean | undefined> = createDescendingSelector(
  selectRequestTaskItem,
  (state) => state?.userAssignCapable,
);

const selectIsAssignable: StateSelector<RequestTaskState, boolean | undefined> = createDescendingSelector(
  selectRequestTask,
  (state) => state?.assignable,
);

const selectIsAssignActionVisible: StateSelector<RequestTaskState, boolean | undefined> = createAggregateSelector(
  selectUserAssignCapable,
  selectIsAssignable,
  (userAssignCapable, assignable) => userAssignCapable && assignable,
);

const selectRequestTaskPayload: StateSelector<RequestTaskState, RequestTaskPayload | undefined> =
  createDescendingSelector(selectRequestTask, (state) => state?.payload);

const selectAssigneeUserId: StateSelector<RequestTaskState, string | undefined> = createDescendingSelector(
  selectRequestTask,
  (state) => state?.assigneeUserId,
);

const selectAssigneeFullName: StateSelector<RequestTaskState, string | undefined> = createDescendingSelector(
  selectRequestTask,
  (state) => state?.assigneeFullName,
);

const selectRequestTaskType: StateSelector<RequestTaskState, RequestTaskDTO['type'] | undefined> =
  createDescendingSelector(selectRequestTask, (state) => state?.type);

const selectRelatedTasks: StateSelector<RequestTaskState, ItemDTO[]> = createAggregateSelector(
  (state) => state?.relatedTasks,
  selectRequestTask,
  (relatedTasks, requestTask) => relatedTasks?.filter((t) => t.taskId !== requestTask?.id) ?? [],
);

const selectTimeline: StateSelector<RequestTaskState, RequestActionInfoDTO[]> = createSelector(
  (state) => state?.timeline,
);

const selectTaskReassignedTo: StateSelector<RequestTaskState, string | null> = createSelector(
  (state) => state?.taskReassignedTo,
);

const selectIsEditable: StateSelector<RequestTaskState, boolean> = createSelector((state) => state.isEditable);

const selectMetadata: StateSelector<RequestTaskState, Record<string, unknown> | undefined> = createSelector(
  (state) => state.metadata,
);

export const requestTaskQuery = {
  selectRequestTaskItem,
  selectAllowedRequestTaskActions,
  selectRequestInfo,
  selectRequestId,
  selectRequestType,
  selectRequestMetadata,
  selectRequestTask,
  selectRequestTaskId,
  selectRequestTaskPayload,
  selectUserAssignCapable,
  selectIsAssignable,
  selectIsAssignActionVisible,
  selectAssigneeUserId,
  selectAssigneeFullName,
  selectRequestTaskType,
  selectRelatedTasks,
  selectRelatedActions,
  selectTimeline,
  selectTaskReassignedTo,
  selectIsEditable,
  selectMetadata,
};
