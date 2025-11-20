import { GovukTableColumn } from '@netz/govuk-components';

import { CcaItemDTO } from 'cca-api';

export type WorkflowItemsAssignmentType = 'assigned-to-me' | 'assigned-to-others' | 'unassigned';

export type Paging = {
  page: number;
  pageSize: number;
};

export type DashboardState = {
  items: CcaItemDTO[];
  isLoading: boolean;
  activeTab: WorkflowItemsAssignmentType;
  totalItems: number;
  paging: {
    page: number;
    pageSize: number;
  };
};

export const DEFAULT_PAGE = 1;
export const DEFAULT_PAGE_SIZE = 50;

export const DEFAULT_TABLE_COLUMNS: GovukTableColumn<CcaItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: false },
  { field: 'businessId', header: 'Target unit ID', isSortable: false },
  { field: 'accountName', header: 'Target unit', isSortable: false },
  { field: 'facilityBusinessId', header: 'Facility', isSortable: false },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: false },
  { field: 'sectorAcronym', header: 'Sector ID', isSortable: false },
];

export const initialState: DashboardState = {
  items: [],
  isLoading: false,
  activeTab: 'assigned-to-me',
  totalItems: 0,
  paging: { page: DEFAULT_PAGE, pageSize: DEFAULT_PAGE_SIZE },
};
