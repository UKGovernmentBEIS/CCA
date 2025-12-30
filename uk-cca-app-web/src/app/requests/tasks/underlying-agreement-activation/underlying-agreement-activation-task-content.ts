import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { UnderlyingAgreementActivationRequestTaskPayload } from 'cca-api';

import UnderlyingAgreementActivationPreContentComponent from './precontent/underlying-agreement-activation-pre-content.component';

const routePrefix = 'underlying-agreement-activation';

export const underlyingAgreementActivationTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Upload target unit assent',
    preContentComponent: UnderlyingAgreementActivationPreContentComponent,
    sections: getAllUnderlyingAgreementActivationSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

function getAllUnderlyingAgreementActivationSections(
  payload: UnderlyingAgreementActivationRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Evidence',
      tasks: [
        {
          linkText: 'Provide evidence',
          status: payload?.sectionsCompleted[PROVIDE_EVIDENCE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/provide-evidence`,
        },
      ],
    },
  ];
}
