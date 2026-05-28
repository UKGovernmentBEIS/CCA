import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { TaskItemStatus } from '@requests/common';

import { EnforcementResponseNoticeWaitForPeerReviewPrecontentComponent } from './precontent/enforcement-response-notice-wait-for-peer-review-precontent.component';

const waitForPeerReviewRoutePrefix = 'enforcement-response-notice-await-peer-review';

export const enforcementResponseNoticeWaitForPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Enforcement response notice sent for peer review',
    preContentComponent: EnforcementResponseNoticeWaitForPeerReviewPrecontentComponent,
    sections: getAllEnforcementResponseNoticeWaitForPeerReviewSections(),
  };
};

export function getAllEnforcementResponseNoticeWaitForPeerReviewSections(): TaskSection[] {
  return [
    {
      title: 'Notice details',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Enforcement response notice',
          link: `${waitForPeerReviewRoutePrefix}/upload-enforcement-response-notice`,
        },
      ],
    },
  ];
}
