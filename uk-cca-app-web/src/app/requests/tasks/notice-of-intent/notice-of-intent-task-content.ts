import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceNoticeOfIntentSubmitRequestTaskPayload } from 'cca-api';

import { noticeOfIntentQuery } from './notice-of-intent.selectors';
import { UPLOAD_NOTICE_OF_INTENT_SUBTASK } from './notice-of-intent.types';
import { NoticeOfIntentPrecontentComponent } from './precontent/notice-of-intent-precontent.component';

const noticeOfIntentRoutePrefix = 'notice-of-intent';

export const noticeOfIntentTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(noticeOfIntentQuery.selectPayload)();

  return {
    header: 'Upload notice of intent',
    preContentComponent: NoticeOfIntentPrecontentComponent,
    sections: getAllNoticeOfIntentSections(payload),
  };
};

export function getAllNoticeOfIntentSections(
  payload: NonComplianceNoticeOfIntentSubmitRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Notice of intent details',
      tasks: [
        {
          linkText: 'Upload notice of intent file',
          status: payload?.sectionsCompleted?.[UPLOAD_NOTICE_OF_INTENT_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${noticeOfIntentRoutePrefix}/upload-notice-of-intent`,
        },
      ],
    },
  ];
}
