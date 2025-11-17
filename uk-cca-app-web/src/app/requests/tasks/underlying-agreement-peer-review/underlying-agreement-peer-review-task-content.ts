import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  calcManageFacilitiesStatus,
  OVERALL_DECISION_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  UNAPeerReviewTaskPayload,
  underlyingAgreementPeerReviewQuery,
} from '@requests/common';

import { UnderlyingAgreementPeerReviewPrecontentComponent } from './precontent/underlying-agreement-peer-review-precontent.component';

const underlyingAgreementPeerReviewRoutePrefix = 'underlying-agreement-peer-review';

export const underlyingAgreementPeerReviewTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(underlyingAgreementPeerReviewQuery.selectPayload)();

  return {
    header: 'Peer review underlying agreement request',
    preContentComponent: UnderlyingAgreementPeerReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementPeerReviewSections(payload),
  };
};

export function getAllUnderlyingAgreementPeerReviewSections(payload: UNAPeerReviewTaskPayload): TaskSection[] {
  return [
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK],
          linkText: 'Target unit details',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/target-unit-details`,
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: calcManageFacilitiesStatus(payload.reviewSectionsCompleted, payload.underlyingAgreement?.facilities),
          linkText: 'Manage facilities',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/manage-facilities`,
        },
      ],
    },
    {
      title: 'Authorisation Details',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK],
          linkText: 'Authorisation and additional evidence',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/authorisation-additional-evidence`,
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[OVERALL_DECISION_SUBTASK],
          linkText: 'Overall decision',
          link: `${underlyingAgreementPeerReviewRoutePrefix}/overall-decision`,
        },
      ],
    },
  ];
}
