import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { Cca3MigrationAccountActivationComponent } from './cca3-migration-account-activation.component';
import { CCA3MigrationRequestTaskPayload } from './types';

const routePrefix = 'cca3-migration-account-activation';

export const cca3MigrationAccountActivationTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(requestTaskQuery.selectRequestTaskPayload)();

  return {
    header: 'Upload target unit assent',
    preContentComponent: Cca3MigrationAccountActivationComponent,
    sections: getCca3MigrationSections(payload),
  };
};

function getCca3MigrationSections(payload: CCA3MigrationRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Evidence',
      tasks: [
        {
          status: payload?.sectionsCompleted[CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/provide-evidence`,
          linkText: 'Provide evidence',
        },
      ],
    },
  ];
}
