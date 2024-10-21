import { inject } from '@angular/core';

import { TaskItem, TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  staticGroupDecisions,
  TaskItemStatus,
} from '@requests/common';

import { UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { UnderlyingAgreementReviewPrecontentComponent } from './precontent/underlying-agreement-review-precontent.component';

const routePrefix = 'underlying-agreement-review';

export const underlyingAgreementReviewTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Review application for underlying agreement',
    preContentComponent: UnderlyingAgreementReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllUnderlyingAgreementSections(payload: UnderlyingAgreementReviewRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: getAllFacilities(payload),
    },
    {
      title: 'Baseline and Targets',
      tasks: [
        {
          status:
            payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] ??
            TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status:
            payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] ??
            TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/target-period-6`,
          linkText: 'TP6 (2024)',
        },
      ],
    },
    {
      title: 'Authorisation details',
      tasks: [
        {
          status:
            payload?.reviewSectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] ?? TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: reviewSectionsCompleted(payload) ? overallDecisionStatus(payload) : TaskItemStatus.CANNOT_START_YET,
          link: reviewSectionsCompleted(payload) ? `${routePrefix}/send-application` : '',
          linkText: 'Overall decision',
        },
      ],
    },
  ];
}

function getAllFacilities(payload: UnderlyingAgreementReviewRequestTaskPayload): TaskItem[] {
  return (
    payload?.underlyingAgreement?.facilities?.map((facility) => ({
      status: payload?.reviewSectionsCompleted?.[facility.facilityId] ?? TaskItemStatus.UNDECIDED,
      link: `${routePrefix}/facility/${facility.facilityId}`,
      linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
    })) ?? []
  );
}

function reviewSectionsCompleted(payload: UnderlyingAgreementReviewRequestTaskPayload): boolean {
  const hasUndecidedSection = Object.keys(payload.reviewSectionsCompleted).some(
    (k) => payload.reviewSectionsCompleted[k] === TaskItemStatus.UNDECIDED,
  );
  if (hasUndecidedSection) return false;
  const sectionsCompleted = staticGroupDecisions.every((s) => payload.reviewGroupDecisions[s]);
  if (!sectionsCompleted) return false;
  return payload.underlyingAgreement.facilities.every((f) => payload.facilitiesReviewGroupDecisions[f.facilityId]);
}

function overallDecisionStatus(payload: UnderlyingAgreementReviewRequestTaskPayload): TaskItemStatus {
  if (payload.determination?.type) {
    return payload.determination.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;
  } else {
    return TaskItemStatus.UNDECIDED;
  }
}
