import { GovukTableColumn } from '@netz/govuk-components';

import { ItemTargetUnitDTO } from 'cca-api';

export const columns: GovukTableColumn<ItemTargetUnitDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: true },
  { field: 'taskAssignee', header: 'Assigned to', isSortable: true },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: true },
];

export const assignedItems: ItemTargetUnitDTO[] = [
  {
    taskType: 'DUMMY_REQUEST_TYPE_APPLICATION_REVIEW',
    taskAssignee: { firstName: 'TEST_FN', lastName: 'TEST_LN' },
    daysRemaining: 10,
  },
];

export const unassignedItems = assignedItems.map((item) => ({ ...item, taskAssignee: null }));
