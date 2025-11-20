import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AuditDetailsCorrectiveActionsSubmitRequestTaskPayload } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from './audit-details-corrective-actions.selectors';
import { CompleteTaskButtonComponent } from './complete-task/complete-task-button.component';
import { AUDIT_DETAILS_SUBTASK, CORRECTIVE_ACTIONS_SUBTASK } from './types';

const routePrefix = 'audit-details-corrective-actions';

export const auditDetailsCorrectiveActionsTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(auditDetailsCorrectiveActionsQuery.selectPayload)();

  return {
    header: 'Audit details and corrective actions',
    preContentComponent: CompleteTaskButtonComponent,
    sections: auditDetailsCorrectiveActionsSections(payload),
  };
};

function auditDetailsCorrectiveActionsSections(
  payload: AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Audit details',
      tasks: [
        {
          status: payload?.sectionsCompleted[AUDIT_DETAILS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/audit-details`,
          linkText: 'Details of the audit',
        },
      ],
    },
    {
      title: 'Corrective actions',
      tasks: [
        {
          status: payload?.sectionsCompleted[CORRECTIVE_ACTIONS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/corrective-actions`,
          linkText: 'Add corrective actions',
        },
      ],
    },
  ];
}
