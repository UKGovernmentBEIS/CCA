import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'cca-api';

export interface RequestTaskState {
  requestTaskItem: RequestTaskItemDTO;
  relatedTasks: ItemDTO[];
  timeline: RequestActionInfoDTO[];
  taskReassignedTo: string;
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
