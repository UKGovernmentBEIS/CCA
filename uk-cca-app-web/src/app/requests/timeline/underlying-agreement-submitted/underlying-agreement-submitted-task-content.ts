import { TaskSection } from '@netz/common/model';
import { transformFacilities } from '@requests/common';

import { Facility } from 'cca-api';

export function getAllUnderlyingAgreementSections(facilities: Facility[], prefix = ''): TaskSection[] {
  return [
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
          linkText: 'Manage facilities list',
        },
        ...transformFacilities(facilities, [], null, prefix),
      ],
    },
    {
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
    },
    {
      title: 'Authorization details',
      tasks: [
        {
          status: '',
          link: `${prefix}authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
  ];
}
