import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { UnderlyingAgreementVariationPeerReviewRequestTaskPayload } from 'cca-api';

import { UnderlyingAgreementVariationWaitForPeerReviewPrecontentComponent } from './precontent/underlying-agreement-variation-wait-for-peer-review-precontent.component';

const waitForPeerReviewRoutePrefix = 'underlying-agreement-variation-await-peer-review';

export const underlyingAgreementVariationWaitForPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Application for underlying agreement variation sent for peer review',
    preContentComponent: UnderlyingAgreementVariationWaitForPeerReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementVariationWaitForPeerReviewSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllUnderlyingAgreementVariationWaitForPeerReviewSections(
  payload: UnderlyingAgreementVariationPeerReviewRequestTaskPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Describe the changes',
          link: `${waitForPeerReviewRoutePrefix}/variation-details`,
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Target unit details',
          link: `${waitForPeerReviewRoutePrefix}/review-target-unit-details`,
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Manage facilities list',
          link: `${waitForPeerReviewRoutePrefix}/manage-facilities`,
        },
      ],
    },
  ];

  if (payload?.underlyingAgreement?.targetPeriod5Details && payload?.underlyingAgreement?.targetPeriod6Details) {
    sections.push({
      title: 'Baseline and Targets',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'TP5 (2021-2022)',
          link: `${waitForPeerReviewRoutePrefix}/target-period-5`,
        },
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'TP6 (2024)',
          link: `${waitForPeerReviewRoutePrefix}/target-period-6`,
        },
      ],
    });
  }

  sections.push(
    {
      title: 'Authorisation details',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Authorisation and additional evidence',
          link: `${waitForPeerReviewRoutePrefix}/authorisation-additional-evidence`,
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: TaskItemStatus.COMPLETED,
          linkText: 'Overall decision',
          link: `${waitForPeerReviewRoutePrefix}/overall-decision`,
        },
      ],
    },
  );

  return sections;
}
