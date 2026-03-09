import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  OPERATOR_ASSENT_DECISION_SUBTASK,
  regulatorLedManageFacilitiesStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAVariationRegulatorLedRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { UnARegulatorLedVariationWaitForPeerReviewPrecontentComponent } from './precontent/underlying-agreement-variation-regulator-led-wait-for-peer-review-precontent.component';

const routePrefix = 'underlying-agreement-variation-regulator-led-await-peer-review';

export const unaRegulatorLedVariationWaitForPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);
  const payload = store.select(requestTaskQuery.selectRequestTaskPayload)();

  return {
    header: 'Underlying agreement variation sent for peer review',
    preContentComponent: UnARegulatorLedVariationWaitForPeerReviewPrecontentComponent,
    sections: getAllUnARegulatorLedVariationWaitForPeerReviewSections(payload),
  };
};

export function getAllUnARegulatorLedVariationWaitForPeerReviewSections(
  payload: UNAVariationRegulatorLedRequestTaskPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload?.sectionsCompleted[VARIATION_DETAILS_SUBTASK],
          linkText: 'Describe the changes',
          link: `${routePrefix}/variation-details`,
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK],
          linkText: 'Target unit details',
          link: `${routePrefix}/review-target-unit-details`,
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: regulatorLedManageFacilitiesStatus(payload),
          linkText: 'Manage facilities list',
          link: `${routePrefix}/manage-facilities`,
        },
      ],
    },
  ];

  if (payload?.underlyingAgreement?.targetPeriod5Details && payload?.underlyingAgreement?.targetPeriod6Details) {
    sections.push({
      title: 'Baseline and Targets',
      tasks: [
        {
          status:
            payload?.sectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] ??
            TaskItemStatus.COMPLETED,
          linkText: 'TP5 (2021-2022)',
          link: `${routePrefix}/target-period-5`,
        },
        {
          status:
            payload?.sectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] ??
            TaskItemStatus.COMPLETED,
          linkText: 'TP6 (2024)',
          link: `${routePrefix}/target-period-6`,
        },
      ],
    });
  }

  sections.push(
    {
      title: 'Authorisation details',
      tasks: [
        {
          status: payload?.sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK],
          linkText: 'Authorisation and additional evidence',
          link: `${routePrefix}/authorisation-additional-evidence`,
        },
      ],
    },
    {
      title: 'Operator assent decision',
      tasks: [
        {
          status: payload?.sectionsCompleted[OPERATOR_ASSENT_DECISION_SUBTASK],
          linkText: 'Determine operator assent',
          link: `${routePrefix}/operator-assent-decision`,
        },
      ],
    },
  );

  return sections;
}
