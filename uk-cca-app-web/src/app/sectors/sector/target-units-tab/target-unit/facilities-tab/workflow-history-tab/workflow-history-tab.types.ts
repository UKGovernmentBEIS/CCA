import type { RequestDetailsSearchResults } from 'cca-api';

export type WorkflowHistoryTabState = {
  workflowsHistory: RequestDetailsSearchResults;
  requestTypes: string[];
  requestStatuses: string[];
  totalItems: number;
  currentPage: number;
  pageSize: number;
};

export enum RequestWorkflowHistoryType {
  SECTOR_MOA = 'Subsistence fees',
}

export enum RequestWorkflowHistoryStatus {
  CANCELLED = 'Cancelled',
  COMPLETED = 'Completed',
  IN_PROGRESS = 'In progress',
  EXPIRED = 'Expired',
}

export const workflowTypesMap: Record<string, string | string[]> = {
  'Facility audit': 'FACILITY_AUDIT',
  'Target period (TP) reporting': 'PERFORMANCE_DATA_FACILITY_PROCESSING',
};

export const workflowStatusesMap: Record<string, string> = {
  Cancelled: 'CANCELLED',
  Completed: 'COMPLETED',
  'In progress': 'IN_PROGRESS',
  Expired: 'EXPIRED',
};
