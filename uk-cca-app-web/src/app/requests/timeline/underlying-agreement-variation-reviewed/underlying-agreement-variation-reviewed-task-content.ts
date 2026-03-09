import { TaskSection } from '@netz/common/model';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  calcManageFacilitiesStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UnderlyingAgreementVariationDecisionRequestActionPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

export function getAllUnderlyingAgreementVariationReviewTimelineSections(
  payload: UnderlyingAgreementVariationDecisionRequestActionPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK],
          link: 'variation-details',
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK],
          link: 'review-target-unit-details',
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: calcManageFacilitiesStatus(payload.reviewSectionsCompleted, payload.underlyingAgreement?.facilities),
          link: 'review-manage-facilities',
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
          link: 'target-period-5',
          linkText: 'TP5 (2021-2022)',
        },
        {
          status: payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS],
          link: 'target-period-6',
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
          status: payload?.reviewSectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK],
          link: 'authorisation-additional-evidence',
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: payload?.determination.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED,
          link: 'overall-decision',
          linkText: 'Overall decision',
        },
      ],
    },
  );

  return sections;
}
