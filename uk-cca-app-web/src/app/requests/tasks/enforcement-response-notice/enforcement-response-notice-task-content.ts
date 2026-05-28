import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload } from 'cca-api';

import { enforcementResponseNoticeQuery } from './enforcement-response-notice.selectors';
import { UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK } from './enforcement-response-notice.types';
import { EnforcementResponseNoticePrecontentComponent } from './precontent/enforcement-response-notice-precontent.component';

const enforcementResponseNoticeRoutePrefix = 'enforcement-response-notice';

export const enforcementResponseNoticeTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(enforcementResponseNoticeQuery.selectPayload)();

  return {
    header: 'Upload enforcement response notice',
    preContentComponent: EnforcementResponseNoticePrecontentComponent,
    sections: getAllEnforcementResponseNoticeSections(payload),
  };
};

export function getAllEnforcementResponseNoticeSections(
  payload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Notice details',
      tasks: [
        {
          linkText: 'Enforcement response notice',
          status:
            payload?.sectionsCompleted?.[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${enforcementResponseNoticeRoutePrefix}/upload-enforcement-response-notice`,
        },
      ],
    },
  ];
}
