import { TaskSection } from '@netz/common/model';

const routePrefix = 'underlying-agreement-variation-submitted';

export function getAllUnderlyingAgreementVariationSections(): TaskSection[] {
  return [
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
    {
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
    },
    {
      title: 'Authorization details',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
  ].filter((item) => item.tasks.length > 0);
}
