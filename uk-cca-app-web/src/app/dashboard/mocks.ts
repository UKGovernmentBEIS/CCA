import { GovukTableColumn } from '@netz/govuk-components';

import { CcaItemDTO } from 'cca-api';

export const columns: GovukTableColumn<CcaItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: false },
  { field: 'businessId', header: 'Target unit ID', isSortable: false },
  { field: 'accountName', header: 'Target unit', isSortable: false },
  { field: 'facilityBusinessId', header: 'Facility', isSortable: false },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: false },
  { field: 'sectorAcronym', header: 'Sector ID', isSortable: false },
];

export const assignedItems: CcaItemDTO[] = [
  {
    taskType: 'UNDERLYING_AGREEMENT_APPLICATION_REVIEW',
    requestType: 'UNDERLYING_AGREEMENT',
    taskId: 1,
    requestId: '1',
    businessId: 'TU-001',
    accountName: 'Acme Manufacturing Ltd',
    facilityBusinessId: 'F-001',
    siteName: 'Leeds Works',
    sectorAcronym: 'FBS',
    daysRemaining: 10,
    isNew: true,
  },
  {
    taskType: 'PERFORMANCE_DATA_UPLOAD_SUBMIT',
    requestType: 'PERFORMANCE_DATA_UPLOAD',
    taskId: 2,
    requestId: '2',
    businessId: 'TU-002',
    accountName: 'North Energy Ltd',
    facilityBusinessId: 'F-002',
    siteName: 'Cardiff Site',
    sectorAcronym: 'CHEM',
    isNew: false,
  },
];

export const unassignedItems = assignedItems.map((item) => ({ ...item, taskAssignee: undefined }));
