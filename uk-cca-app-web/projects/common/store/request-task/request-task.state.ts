import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'cca-api';

export interface RequestTaskState {
  requestTaskItem: RequestTaskItemDTO | null;
  relatedTasks: ItemDTO[];
  timeline: RequestActionInfoDTO[];
  taskReassignedTo: string | null;
  isEditable: boolean;
  metadata?: Record<string, unknown>;
}

export const initialRequestTaskState: RequestTaskState = {
  requestTaskItem: null,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: false,
};
