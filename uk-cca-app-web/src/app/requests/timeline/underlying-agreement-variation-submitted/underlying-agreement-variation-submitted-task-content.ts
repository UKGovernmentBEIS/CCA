import { TaskItem, TaskSection } from '@netz/common/model';
import { UNAVariationRequestTaskPayload } from '@requests/common';

const routePrefix = 'underlying-agreement-variation-submitted';

export function getAllUnderlyingAgreementVariationSections(payload: UNAVariationRequestTaskPayload): TaskSection[] {
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
        ...getAllFacilities(payload),
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
  ];
}

function getAllFacilities(payload: UNAVariationRequestTaskPayload): TaskItem[] {
  return (
    payload?.underlyingAgreement?.facilities?.map((facility) => ({
      status: '',
      link: `${routePrefix}/facility/${facility.facilityId}`,
      linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
    })) ?? []
  );
}
