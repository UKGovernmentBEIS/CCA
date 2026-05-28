import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceConclusionSubmitRequestTaskPayload } from 'cca-api';

import { ConclusionPrecontentComponent } from './precontent/precontent.component';
import { NON_COMPLIANCE_CONCLUSION_SUBTASK } from './types';

const conclusionRoutePrefix = 'non-compliance-conclusion';

export const nonComplianceConclusionTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Provide non-compliance conclusion',
    preContentComponent: ConclusionPrecontentComponent,
    sections: getAllConclusionSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload as NonComplianceConclusionSubmitRequestTaskPayload,
    ),
  };
};

export function getAllConclusionSections(payload: NonComplianceConclusionSubmitRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Non-compliance conclusion details',
      tasks: [
        {
          linkText: 'Provide conclusion of non-compliance',
          status: payload?.sectionsCompleted?.[NON_COMPLIANCE_CONCLUSION_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: conclusionRoutePrefix,
        },
      ],
    },
  ];
}
