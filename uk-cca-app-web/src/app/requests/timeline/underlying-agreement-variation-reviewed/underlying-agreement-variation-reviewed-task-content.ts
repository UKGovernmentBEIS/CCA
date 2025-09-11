import { TaskItem, TaskSection } from '@netz/common/model';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  sortFacilitiesById,
  TaskItemStatus,
  UnderlyingAgreementVariationDecisionRequestActionPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { Facility } from 'cca-api';

function activeFacilitiesToTaskItems(
  facilities: Facility[],
  reviewSectionsCompleted: Record<string, string>,
): TaskItem[] {
  return sortFacilitiesById(facilities)
    .filter((facility) => ['NEW', 'LIVE'].includes(facility.status))
    .map((facility) => ({
      status: reviewSectionsCompleted?.[facility.facilityId] ?? '',
      link: `facility/${facility.facilityId}`,
      linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
    }));
}

function excludedFacilitiesToTaskItems(
  facilities: Facility[],
  reviewSectionsCompleted: Record<string, string>,
): TaskItem[] {
  return sortFacilitiesById(facilities)
    .filter((facility) => facility.status === 'EXCLUDED')
    .map((facility) => ({
      status: reviewSectionsCompleted?.[facility.facilityId] ?? '',
      link: `facility/${facility.facilityId}`,
      linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
    }));
}

export function getAllUnderlyingAgreementVariationReviewTimelineSections(
  payload: UnderlyingAgreementVariationDecisionRequestActionPayload,
): TaskSection[] {
  return [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK],
          link: `variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK],
          link: `review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: activeFacilitiesToTaskItems(payload?.underlyingAgreement?.facilities, payload?.reviewSectionsCompleted),
    },
    {
      title: 'Excluded Facilities',
      tasks: excludedFacilitiesToTaskItems(payload?.underlyingAgreement?.facilities, payload?.reviewSectionsCompleted),
    },
    {
      title: 'Baseline and Targets',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS],
          link: `target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status: payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS],
          link: `target-period-6`,
          linkText: 'TP6 (2024)',
        },
      ],
    },
    {
      title: 'Authorisation details',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK],
          link: `authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: payload.determination.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED,
          link: 'overall-decision',
          linkText: 'Overall decision',
        },
      ],
    },
  ].filter((item) => item.tasks.length > 0);
}
