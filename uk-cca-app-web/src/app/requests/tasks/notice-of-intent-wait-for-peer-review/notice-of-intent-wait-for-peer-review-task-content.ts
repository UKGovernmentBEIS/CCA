import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { TaskItemStatus } from '@requests/common';

import { NoticeOfIntentWaitForPeerReviewPrecontentComponent } from './precontent/notice-of-intent-wait-for-peer-review-precontent.component';

const waitForPeerReviewRoutePrefix = 'notice-of-intent-await-peer-review';

export const noticeOfIntentWaitForPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Notice of intent sent for peer review',
    preContentComponent: NoticeOfIntentWaitForPeerReviewPrecontentComponent,
    sections: getAllNoticeOfIntentWaitForPeerReviewSections(),
  };
};

export function getAllNoticeOfIntentWaitForPeerReviewSections(): TaskSection[] {
  return [
    {
      title: 'Notice of intent details',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Upload notice of intent file',
          link: `${waitForPeerReviewRoutePrefix}/upload-notice-of-intent`,
        },
      ],
    },
  ];
}
