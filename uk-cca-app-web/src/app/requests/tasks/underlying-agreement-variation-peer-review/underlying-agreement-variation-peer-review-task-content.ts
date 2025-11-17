import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  calcManageFacilitiesStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { UnderlyingAgreementVariationPeerReviewRequestTaskPayload } from 'cca-api';

import { UnderlyingAgreementVariationPeerReviewPrecontentComponent } from './precontent/underlying-agreement-variation-peer-review-precontent.component';

const underlyingAgreementVariationPeerReviewRoutePrefix = 'underlying-agreement-variation-peer-review';

export const underlyingAgreementVariationPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Peer review application for underlying agreement variation',
    preContentComponent: UnderlyingAgreementVariationPeerReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementVariationPeerReviewSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllUnderlyingAgreementVariationPeerReviewSections(
  payload: UnderlyingAgreementVariationPeerReviewRequestTaskPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted?.[VARIATION_DETAILS_SUBTASK],
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted?.[REVIEW_TARGET_UNIT_DETAILS_SUBTASK],
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: calcManageFacilitiesStatus(
            payload?.reviewSectionsCompleted || {},
            payload.underlyingAgreement?.facilities,
          ),
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/manage-facilities`,
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
          status: payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS],
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status: payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS],
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/target-period-6`,
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
          status: payload?.reviewSectionsCompleted?.[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK],
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: payload?.determination?.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED,
          link: `${underlyingAgreementVariationPeerReviewRoutePrefix}/overall-decision`,
          linkText: 'Overall decision',
        },
      ],
    },
  );

  return sections;
}
