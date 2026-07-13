import type { RequestDetailsSearchResults } from 'cca-api';

export type WorkflowHistoryTabState = {
  workflowsHistory: RequestDetailsSearchResults;
  requestTypes: string[];
  requestStatuses: string[];
  totalItems: number;
  currentPage: number;
  pageSize: number;
};

export enum RequestWorkflowHistoryType {}

export enum RequestWorkflowHistoryStatus {
  CANCELLED = 'Cancelled',
  COMPLETED = 'Completed',
  IN_PROGRESS = 'In progress',
}

export const workflowTypesMap: Record<string, string> = {
  'Subsistence fees': 'SECTOR_MOA',
  'Target period (TP) reporting': 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD',
};

export const workflowStatusesMap: Record<string, string> = {
  Cancelled: 'CANCELLED',
  Completed: 'COMPLETED',
  'In progress': 'IN_PROGRESS',
};
