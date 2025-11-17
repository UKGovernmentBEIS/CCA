import { TaskSection } from '@netz/common/model';

import { UnderlyingAgreementVariationPayload } from 'cca-api';

const routePrefix = 'underlying-agreement-variation-submitted';

export function getAllUnderlyingAgreementVariationSections(una: UnderlyingAgreementVariationPayload): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/manage-facilities`,
          linkText: 'Manage facilities list',
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
          link: `${routePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status: '',
          link: `${routePrefix}/target-period-6`,
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
        link: `${routePrefix}/authorisation-additional-evidence`,
        linkText: 'Authorisation and additional evidence',
      },
    ],
  });

  return sections.filter((item) => item.tasks.length > 0);
}
