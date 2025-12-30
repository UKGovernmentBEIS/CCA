import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationWithdrawRequestTaskPayload } from 'cca-api';

import { WithdrawAdminTerminationPrecontentComponent } from './precontent/withdraw-admin-termination-precontent.component';
import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from './types';

const withdrawAdminTerminationRoutePrefix = 'withdraw-admin-termination';

export const withdrawAdminTerminationTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Withdraw admin termination',
    preContentComponent: WithdrawAdminTerminationPrecontentComponent,
    sections: getAllWithdrawAdminTerminationSections(requestTaskStore.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllWithdrawAdminTerminationSections(
  payload: AdminTerminationWithdrawRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Withdrawal details',
      tasks: [
        {
          link: `${withdrawAdminTerminationRoutePrefix}/reason-for-withdraw-admin-termination`,
          status:
            payload?.sectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          linkText: 'Reason for withdrawing the admin termination',
        },
      ],
    },
  ];
}
