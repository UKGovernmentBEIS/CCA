import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationWaitForPeerReviewPrecontentComponent } from './precontent/admin-termination-wait-for-peer-review-precontent.component';

const waitForPeerReviewRoutePrefix = 'admin-termination-await-peer-review';

export const adminTerminationWaitForPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Admin termination sent for peer review',
    preContentComponent: AdminTerminationWaitForPeerReviewPrecontentComponent,
    sections: getAllAdminTerminationWaitForPeerReviewSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllAdminTerminationWaitForPeerReviewSections(_payload: any): TaskSection[] {
  return [
    {
      title: 'Termination Details',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Reason for admin termination',
          link: `${waitForPeerReviewRoutePrefix}/reason-for-admin-termination`,
        },
      ],
    },
  ];
}
