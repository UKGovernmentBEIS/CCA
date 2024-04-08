import { GovukTableColumn } from 'govuk-components';

import { ItemDTO } from 'cca-api';

export const columns: GovukTableColumn<ItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: true },
  { field: 'taskAssignee', header: 'Assigned to', isSortable: true },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: true },
  { field: 'accountName', header: 'Installation', isSortable: true },
];

export const assignedItems: ItemDTO[] = [
  {
    taskType: 'ORGANISATION_ACCOUNT_OPENING_APPLICATION_REVIEW',
    taskAssignee: { firstName: 'TEST_FN', lastName: 'TEST_LN' },
    daysRemaining: 10,
    accountName: 'ACCOUNT_3',
  },
];

export const unassignedItems = assignedItems.map((item) => ({ ...item, taskAssignee: null }));
