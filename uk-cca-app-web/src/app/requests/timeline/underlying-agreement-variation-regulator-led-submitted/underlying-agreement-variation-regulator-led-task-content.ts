import { TaskSection } from '@netz/common/model';

import { UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload } from 'cca-api';

export function getAllUnARegulatorLedVariationTimelineSections(
  payload: UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: '',
          link: 'variation-details',
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: '',
          link: 'review-target-unit-details',
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: '',
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
          status: '',
          link: 'target-period-5',
          linkText: 'TP5 (2021-2022)',
        },
        {
          status: '',
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
          status: '',
          link: 'authorisation-additional-evidence',
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Operator assent decision',
      tasks: [
        {
          status: '',
          link: 'operator-assent-decision',
          linkText: 'Determine operator assent',
        },
      ],
    },
  );

  return sections;
}
