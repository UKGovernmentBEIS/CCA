import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload } from 'cca-api';

import { UPLOAD_NOTICE_OF_INTENT_SUBTASK } from '../notice-of-intent/notice-of-intent.types';
import { NoticeOfIntentPeerReviewPrecontentComponent } from './precontent/notice-of-intent-peer-review-precontent.component';

const noticeOfIntentPeerReviewRoutePrefix = 'notice-of-intent-peer-review';

export const noticeOfIntentPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Peer review notice of intent',
    preContentComponent: NoticeOfIntentPeerReviewPrecontentComponent,
    sections: getAllNoticeOfIntentPeerReviewSections(requestTaskStore.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllNoticeOfIntentPeerReviewSections(
  payload: NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Notice of intent details',
      tasks: [
        {
          status: payload?.sectionsCompleted?.[UPLOAD_NOTICE_OF_INTENT_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          linkText: 'Upload notice of intent file',
          link: `${noticeOfIntentPeerReviewRoutePrefix}/upload-notice-of-intent`,
        },
      ],
    },
  ];
}
