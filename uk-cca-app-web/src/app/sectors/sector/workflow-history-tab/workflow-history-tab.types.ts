import type { RequestDetailsSearchResults, RequestMetadata } from 'cca-api';

/**
 * The backend returns extra metadata fields (targetPeriodType, reportType, submittedDate)
 * that are not declared in the generated OpenAPI model. This type acknowledges them so
 * templates can be type-checked without $any escapes.
 */
export type WorkflowRequestMetadata = RequestMetadata & {
  targetPeriodType?: string;
  reportType?: string;
  submittedDate?: string;
  targetYear?: string;
};

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
  'Performance account template (PAT) reporting': 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD',
  'Subsistence fees': 'SECTOR_MOA',
  'Target period (TP) reporting': 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD',
};

export const workflowStatusesMap: Record<string, string> = {
  Cancelled: 'CANCELLED',
  Completed: 'COMPLETED',
  'In progress': 'IN_PROGRESS',
};
