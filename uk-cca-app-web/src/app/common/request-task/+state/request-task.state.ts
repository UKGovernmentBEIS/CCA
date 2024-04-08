import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'cca-api';

export interface RequestTaskState {
  requestTaskItem: RequestTaskItemDTO;
  relatedTasks: ItemDTO[];
  timeline: RequestActionInfoDTO[];
  taskReassignedTo: string;
  isEditable: boolean;
  metadata?: { [key: string]: unknown };
}

export const initialState: RequestTaskState = {
  requestTaskItem: null,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: false,
};
