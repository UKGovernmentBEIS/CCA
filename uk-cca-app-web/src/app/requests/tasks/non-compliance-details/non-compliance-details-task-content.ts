import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceDetailsSubmitRequestTaskPayload } from 'cca-api';

import { NonComplianceCompleteTaskButtonComponent } from './complete-task/complete-task-button.component';
import { NON_COMPLIANCE_DETAILS_SUBTASK } from './types';

const nonComplianceRoutePrefix = 'non-compliance';

export const nonComplianceDetailsTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Provide non-compliance details',
    preContentComponent: NonComplianceCompleteTaskButtonComponent,
    sections: getAllNonComplianceSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload as NonComplianceDetailsSubmitRequestTaskPayload,
    ),
  };
};

export function getAllNonComplianceSections(payload: NonComplianceDetailsSubmitRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Type of non-compliance',
      tasks: [
        {
          linkText: 'Non-compliance details',
          status: payload?.sectionsCompleted?.[NON_COMPLIANCE_DETAILS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: nonComplianceRoutePrefix,
        },
      ],
    },
  ];
}
