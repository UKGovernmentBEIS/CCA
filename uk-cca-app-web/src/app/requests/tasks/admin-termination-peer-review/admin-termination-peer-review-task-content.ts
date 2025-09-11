import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationPeerReviewRequestTaskPayload } from 'cca-api';

import { AdminTerminationPeerReviewPrecontentComponent } from './precontent/admin-termination-peer-review-precontent.component';

const adminTerminationPeerReviewRoutePrefix = 'admin-termination-peer-review';

export const adminTerminationPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Peer review admin termination request',
    preContentComponent: AdminTerminationPeerReviewPrecontentComponent,
    sections: getAllAdminTerminationPeerReviewSections(requestTaskStore.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllAdminTerminationPeerReviewSections(
  payload: AdminTerminationPeerReviewRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Termination details',
      tasks: [
        {
          status: payload?.sectionsCompleted['adminTerminationReasonDetails'] ?? TaskItemStatus.NOT_STARTED,
          linkText: 'Reason for admin termination',
          link: `${adminTerminationPeerReviewRoutePrefix}/reason-for-admin-termination`,
        },
      ],
    },
  ];
}
