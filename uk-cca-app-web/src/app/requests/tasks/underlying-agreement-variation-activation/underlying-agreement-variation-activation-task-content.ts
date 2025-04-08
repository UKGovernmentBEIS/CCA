import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { UnderlyingAgreementVariationActivationRequestTaskPayload } from 'cca-api';

import UnderlyingAgreementVariationActivationPreContentComponent from './underlying-agreement-variation-activation-pre-content.component';

const routePrefix = 'underlying-agreement-variation-activation';

export const underlyingAgreementVariationActivationTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Upload target unit assent on variation',
    preContentComponent: UnderlyingAgreementVariationActivationPreContentComponent,
    sections: getAllUnderlyingAgreementVariationActivationSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

function getAllUnderlyingAgreementVariationActivationSections(
  payload: UnderlyingAgreementVariationActivationRequestTaskPayload,
): TaskSection[] {
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
