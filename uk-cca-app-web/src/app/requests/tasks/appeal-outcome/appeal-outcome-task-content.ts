import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { isAppealOutcomeCompleted } from './appeal-outcome.guard';
import { appealOutcomeQuery } from './appeal-outcome.selectors';
import { APPEAL_OUTCOME_SUBTASK, AppealOutcomeRequestTaskPayload } from './types';

const appealOutcomeRoutePrefix = 'appeal-outcome';

export const appealOutcomeTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(appealOutcomeQuery.selectPayload)();

  return {
    header: 'Provide appeal outcome',
    sections: getAllAppealOutcomeSections(payload),
  };
};

export function getAllAppealOutcomeSections(payload: AppealOutcomeRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Provide appeal outcome',
      tasks: [
        {
          linkText: 'Appeal outcome details',
          status:
            payload?.sectionsCompleted?.[APPEAL_OUTCOME_SUBTASK] ??
            (isAppealOutcomeCompleted(payload?.appealOutcome)
              ? TaskItemStatus.IN_PROGRESS
              : TaskItemStatus.NOT_STARTED),
          link: appealOutcomeRoutePrefix,
        },
      ],
    },
  ];
}
