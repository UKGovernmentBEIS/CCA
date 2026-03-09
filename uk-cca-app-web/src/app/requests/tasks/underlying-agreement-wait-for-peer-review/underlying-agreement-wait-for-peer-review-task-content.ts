import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { UnderlyingAgreementWaitForPeerReviewPrecontentComponent } from './precontent/underlying-agreement-wait-for-peer-review-precontent.component';

const waitForPeerReviewRoutePrefix = 'underlying-agreement-await-peer-review';

export const underlyingAgreementWaitForPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Underlying agreement sent for peer review',
    preContentComponent: UnderlyingAgreementWaitForPeerReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementWaitForPeerReviewSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllUnderlyingAgreementWaitForPeerReviewSections(_payload: any): TaskSection[] {
  return [
    {
      title: 'Target unit details',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Target unit details',
          link: `${waitForPeerReviewRoutePrefix}/target-unit-details`,
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Manage facilities',
          link: `${waitForPeerReviewRoutePrefix}/manage-facilities`,
        },
      ],
    },
    {
      title: 'Authorisation and additional evidence',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Authorisation and additional evidence',
          link: `${waitForPeerReviewRoutePrefix}/authorisation-additional-evidence`,
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Overall decision',
          link: `${waitForPeerReviewRoutePrefix}/overall-decision`,
        },
      ],
    },
  ];
}
