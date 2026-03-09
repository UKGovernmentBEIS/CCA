import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  filterEditableTaskLinks,
  OPERATOR_ASSENT_DECISION_SUBTASK,
  regulatorLedManageFacilitiesStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAVariationRegulatorLedRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { UnderlyingAgreementRegulatorLedVariationPrecontentComponent } from './precontent/underlying-agreement-variation-review-precontent.component';

const routePrefix = 'underlying-agreement-variation-regulator-led';

export const underlyingAgreementVariationRegulatorLedTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);
  const payload = store.select(requestTaskQuery.selectRequestTaskPayload)();
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sections = getAllUnAVariationRegulatorLedSections(payload);

  return {
    header: 'Vary the underlying agreement',
    preContentComponent: UnderlyingAgreementRegulatorLedVariationPrecontentComponent,
    sections: filterEditableTaskLinks(sections, isEditable),
  };
};

export function getAllUnAVariationRegulatorLedSections(
  payload: UNAVariationRegulatorLedRequestTaskPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload?.sectionsCompleted[VARIATION_DETAILS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.UNCHANGED,
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: regulatorLedManageFacilitiesStatus(payload),
          link: `${routePrefix}/manage-facilities`,
          linkText: 'Manage facilities',
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
            TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status:
            payload?.sectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] ??
            TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/target-period-6`,
          linkText: 'TP6 (2024)',
        },
      ],
    });
  }

  sections.push(
    {
      title: 'Authorisation details',
      tasks: [
        {
          status: payload?.sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Operator assent decision',
      tasks: [
        {
          status: payload?.sectionsCompleted[OPERATOR_ASSENT_DECISION_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/operator-assent-decision`,
          linkText: 'Determine operator assent',
        },
      ],
    },
  );

  return sections;
}
