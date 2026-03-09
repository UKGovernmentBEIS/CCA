import { TaskSection } from '@netz/common/model';

import { UnderlyingAgreementPayload } from 'cca-api';

export function getAllUnderlyingAgreementSections(una: UnderlyingAgreementPayload, prefix = ''): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Target unit',
      tasks: [
        {
          status: '',
          link: `${prefix}review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: '',
          link: `${prefix}manage-facilities`,
          linkText: 'Manage facilities',
        },
      ],
    },
  ];

  if (una?.targetPeriod5Details && una?.targetPeriod6Details) {
    sections.push({
      title: 'Baseline and Targets',
      tasks: [
        {
          status: '',
          link: `${prefix}target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status: '',
          link: `${prefix}target-period-6`,
          linkText: 'TP6 (2024)',
        },
      ],
    });
  }

  sections.push({
    title: 'Authorization details',
    tasks: [
      {
        status: '',
        link: `${prefix}authorisation-additional-evidence`,
        linkText: 'Authorisation and additional evidence',
      },
    ],
  });

  return sections;
}
