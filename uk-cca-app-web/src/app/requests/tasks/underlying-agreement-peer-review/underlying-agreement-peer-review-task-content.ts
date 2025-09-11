import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import { calcManageFacilitiesStatus, UNAPeerReviewTaskPayload } from '@requests/common';

import { UnderlyingAgreementPeerReviewPrecontentComponent } from './precontent/underlying-agreement-peer-review-precontent.component';

const underlyingAgreementPeerReviewRoutePrefix = 'underlying-agreement-peer-review';

export const underlyingAgreementPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const requestTaskStore = inject(RequestTaskStore);

  return {
    header: 'Peer review underlying agreement request',
    preContentComponent: UnderlyingAgreementPeerReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementPeerReviewSections(
      requestTaskStore.state?.requestTaskItem?.requestTask?.payload,
    ),
  };
};

export function getAllUnderlyingAgreementPeerReviewSections(payload: UNAPeerReviewTaskPayload): TaskSection[] {
  return [
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted['underlyingAgreementTargetUnitDetails'],
          linkText: 'Target unit details',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/target-unit-details`,
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: calcManageFacilitiesStatus(payload.reviewSectionsCompleted),
          linkText: 'Manage facilities',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/manage-facilities`,
        },
      ],
    },
    {
      title: 'Authorisation Details',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted['authorisationAndAdditionalEvidence'],
          linkText: 'Authorisation and additional evidence',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/authorisation-additional-evidence`,
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted['overallDecision'],
          linkText: 'Overall decision',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/overall-decision`,
        },
      ],
    },
  ];
}
