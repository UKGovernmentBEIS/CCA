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
  TargetUnitMoA = 'Subsistence fees',
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
  Migrated = 'Migrated',
}

export const workflowTypesMap: Record<string, string> = {
  'Admin termination': 'ADMIN_TERMINATION',
  'Subsistence fees': 'TARGET_UNIT_MOA',
  'Target unit account creation': 'TARGET_UNIT_ACCOUNT_CREATION',
  'Underlying agreement application': 'UNDERLYING_AGREEMENT',
  Variation: 'UNDERLYING_AGREEMENT_VARIATION',
  'Voluntary termination': 'VOLUNTARY_TERMINATION',
  'Target period reporting (TPR)': 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING',
};

export const workflowStatusesMap: Record<string, string> = {
  Approved: 'APPROVED',
  Cancelled: 'CANCELLED',
  Completed: 'COMPLETED',
  'In progress': 'IN_PROGRESS',
  Rejected: 'REJECTED',
  Withdrawn: 'WITHDRAWN',
  Migrated: 'MIGRATED',
};

export const originalOrder = (): number => {
  return 0;
};
