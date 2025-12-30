import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

import { AdminTerminationPrecontentComponent } from './precontent/admin-termination-precontent.component';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from './types';

const adminTerminationRoutePrefix = 'admin-termination';

export const adminTerminationTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Admin termination',
    preContentComponent: AdminTerminationPrecontentComponent,
    sections: getAllAdminTerminationSections(requestTaskStore.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllAdminTerminationSections(payload: AdminTerminationSubmitRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Termination details',
      tasks: [
        {
          linkText: 'Reason for admin termination',
          status: payload?.sectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${adminTerminationRoutePrefix}/reason-for-admin-termination`,
        },
      ],
    },
  ];
}
