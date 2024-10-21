import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from './admin-termination-final-decision.helper';
import { AdminTerminationFinalDecisionPrecontentComponent } from './precontent/admin-termination-final-decision-precontent.component';

const adminTerminationFinalDecisionRoutePrefix = 'admin-termination-final-decision';

export const adminTerminationFinalDecisionTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Admin termination final decision',
    preContentComponent: AdminTerminationFinalDecisionPrecontentComponent,
    sections: getAllAdminTerminationFinalDecisionSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllAdminTerminationFinalDecisionSections(
  payload: AdminTerminationFinalDecisionRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Decision',
      tasks: [
        {
          name: ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
          status: payload?.sectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          linkText: 'Final decision',
          link: `${adminTerminationFinalDecisionRoutePrefix}/final-decision-reason`,
        },
      ],
    },
  ];
}
