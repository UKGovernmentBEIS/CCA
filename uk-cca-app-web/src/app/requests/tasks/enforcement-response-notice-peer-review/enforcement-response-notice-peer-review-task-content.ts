import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload,
  TaskItemStatus,
  UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK,
} from '@requests/common';

import { EnforcementResponseNoticePeerReviewPrecontentComponent } from './precontent/enforcement-response-notice-peer-review-precontent.component';

const enforcementResponseNoticePeerReviewRoutePrefix = 'enforcement-response-notice-peer-review';

export const enforcementResponseNoticePeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Peer review enforcement response notice',
    preContentComponent: EnforcementResponseNoticePeerReviewPrecontentComponent,
    sections: getAllEnforcementResponseNoticePeerReviewSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllEnforcementResponseNoticePeerReviewSections(
  payload: NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Notice details',
      tasks: [
        {
          status:
            payload?.sectionsCompleted?.[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          linkText: 'Enforcement response notice',
          link: `${enforcementResponseNoticePeerReviewRoutePrefix}/upload-enforcement-response-notice`,
        },
      ],
    },
  ];
}
