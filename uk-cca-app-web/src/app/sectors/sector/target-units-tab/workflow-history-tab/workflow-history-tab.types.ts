import type { RequestDetailsSearchResults } from 'cca-api';

export type WorkflowHistoryTabState = {
  currentPage: number;
  workflowsHistory: RequestDetailsSearchResults;
  requestTypes: string[];
  requestStatuses: string[];
  totalItems: number;
};

export enum RequestWorkflowHistoryType {
  SECTOR_MOA = 'Subsistence fees',
}

export enum RequestWorkflowHistoryStatus {
  CANCELLED = 'Cancelled',
  COMPLETED = 'Completed',
  IN_PROGRESS = 'In progress',
}

export const workflowTypesMap: Record<string, string> = {
  'Subsistence fees': 'SECTOR_MOA',
};

export const workflowStatusesMap: Record<string, string> = {
  Cancelled: 'CANCELLED',
  Completed: 'COMPLETED',
  'In progress': 'IN_PROGRESS',
};

export const originalOrder = (): number => {
  return 0;
};
