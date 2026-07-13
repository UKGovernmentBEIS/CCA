import { GovukSelectOption, GovukTableColumn } from '@netz/govuk-components';

import { CcaItemDTO, ItemSearchCriteriaDTO } from 'cca-api';

export type Paging = {
  page: number;
  pageSize: number;
};

export const DEFAULT_PAGE = 1;
export const DEFAULT_PAGE_SIZE = 30;

export type DashboardOrderBy = NonNullable<ItemSearchCriteriaDTO['orderBy']>;

export const DEFAULT_CRITERIA: ItemSearchCriteriaDTO = {
  orderBy: 'NEWEST_FIRST',
  requestType: undefined,
  searchTerm: undefined,
};

export const SORT_OPTIONS: GovukSelectOption<DashboardOrderBy>[] = [
  { value: 'NEWEST_FIRST', text: 'Newest first' },
  { value: 'OLDEST_FIRST', text: 'Oldest first' },
];

export type WorkflowRequestType =
  | 'ADMIN_TERMINATION'
  | 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING'
  | 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING'
  | 'FACILITY_AUDIT'
  | 'NON_COMPLIANCE'
  | 'PERFORMANCE_DATA_DOWNLOAD'
  | 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD'
  | 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM'
  | 'PERFORMANCE_DATA_UPLOAD'
  | 'TARGET_UNIT_ACCOUNT_CREATION'
  | 'UNDERLYING_AGREEMENT'
  | 'UNDERLYING_AGREEMENT_VARIATION';

const WORKFLOW_REQUEST_TYPE_LABELS: Record<WorkflowRequestType, string> = {
  ADMIN_TERMINATION: 'Admin termination',
  CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING: 'CCA2 extension',
  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING: 'CCA3 migration',
  FACILITY_AUDIT: 'Facility audit',
  NON_COMPLIANCE: 'Non-compliance',
  PERFORMANCE_DATA_DOWNLOAD: 'TP reporting (TP6) - Download spreadsheets',
  PERFORMANCE_DATA_FACILITY_DATA_UPLOAD: 'TP reporting (TP7, TP8, TP9) - Upload CSV file',
  PERFORMANCE_DATA_FACILITY_DIGITAL_FORM: 'TP reporting (TP7, TP8, TP9) - Submit form',
  PERFORMANCE_DATA_UPLOAD: 'TP reporting (TP6) - Upload spreadsheets',
  TARGET_UNIT_ACCOUNT_CREATION: 'Target unit account creation',
  UNDERLYING_AGREEMENT: 'Underlying agreement application',
  UNDERLYING_AGREEMENT_VARIATION: 'Variation',
};

export const WORKFLOW_FILTER_OPTIONS: GovukSelectOption<WorkflowRequestType | null>[] = [
  { value: null, text: 'All' },
  ...Object.entries(WORKFLOW_REQUEST_TYPE_LABELS)
    .map(([requestType, label]) => ({
      value: requestType as WorkflowRequestType,
      text: label,
    }))
    .sort((a, b) => a.text.localeCompare(b.text)),
];

export const DEFAULT_TABLE_COLUMNS: GovukTableColumn<CcaItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: false },
  { field: 'businessId', header: 'Target unit ID', isSortable: false },
  { field: 'accountName', header: 'Target unit', isSortable: false },
  { field: 'facilityBusinessId', header: 'Facility', isSortable: false },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: false },
  { field: 'sectorAcronym', header: 'Sector ID', isSortable: false },
];
