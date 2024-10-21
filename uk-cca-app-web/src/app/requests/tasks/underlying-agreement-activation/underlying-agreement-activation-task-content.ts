import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { UNAActivationRequestTaskPayload } from './underlying-agreement-activation.types';
import UnderlyingAgreementActivationPreContentComponent from './underlying-agreement-activation-pre-content.component';

const routePrefix = 'underlying-agreement-activation';

export const underlyingAgreementActivationTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Upload target unit assent',
    preContentComponent: UnderlyingAgreementActivationPreContentComponent,
    sections: getAllUnderlyingAgreementActivationSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

function getAllUnderlyingAgreementActivationSections(payload: UNAActivationRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Evidence',
      tasks: [
        {
          status: payload?.sectionsCompleted[PROVIDE_EVIDENCE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/provide-evidence`,
          linkText: 'Provide evidence',
        },
      ],
    },
  ];
}
