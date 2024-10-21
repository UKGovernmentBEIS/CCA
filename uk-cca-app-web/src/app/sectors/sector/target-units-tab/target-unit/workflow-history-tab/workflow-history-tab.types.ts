import { RequestDetailsSearchResults } from 'cca-api';

export type WorkflowHistoryTabState = {
  currentPage: number;
  workflowsHistory: RequestDetailsSearchResults;
  requestTypes: string[];
  requestStatuses: string[];
  totalItems: number;
};

export enum RequestWorkflowHistoryType {
  AdminTermination = 'Admin termination',
  TargetUnitAccountCreation = 'Target unit account creation',
  UnderlyingAgreementApplication = 'Underlying agreement application',
  Variation = 'Variation',
  VoluntaryTermination = 'Voluntary termination',
}

export enum RequestWorkflowHistoryStatus {
  Approved = 'Approved',
  Cancelled = 'Cancelled',
  Completed = 'Completed',
  InProgress = 'In progress',
  Rejected = 'Rejected',
  Withdrawn = 'Withdrawn',
}

export const workflowTypesMap: Record<string, string> = {
  'Admin termination': 'ADMIN_TERMINATION',
  'Target unit account creation': 'TARGET_UNIT_ACCOUNT_CREATION',
  'Underlying agreement application': 'UNDERLYING_AGREEMENT',
  Variation: 'VARIATION',
  'Voluntary termination': 'VOLUNTARY_TERMINATION',
};

export const workflowStatusesMap: Record<string, string> = {
  Approved: 'APPROVED',
  Cancelled: 'CANCELLED',
  Completed: 'COMPLETED',
  'In progress': 'IN_PROGRESS',
  Rejected: 'REJECTED',
  Withdrawn: 'WITHDRAWN',
};

export const originalOrder = (): number => {
  return 0;
};
