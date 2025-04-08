import { TaskItem } from '@netz/common/model';

import { Facility } from 'cca-api';

import { TaskItemStatus } from './task-item-status';

export const transformFacilities = (
  facilities: Facility[],
  statuses: Facility['status'][],
  sections: Record<string, string>,
  linkPrefix: string,
  linkSuffix?: string,
  alternativeStatus?: TaskItemStatus,
): TaskItem[] => {
  const prefix = linkPrefix ? `${linkPrefix}/` : '';
  const suffix = linkSuffix ? `/${linkSuffix}` : '';
  const altStatus = alternativeStatus ?? '';

  return (
    facilities
      ?.filter((facility) => (statuses.length ? statuses.includes(facility.status) : facility))
      ?.sort((fa, fb) => fa.facilityId.localeCompare(fb.facilityId, 'en-GB', { numeric: true, sensitivity: 'base' }))
      ?.map((facility) => ({
        status: sections?.[facility.facilityId] ?? altStatus,
        link: `${prefix}facility/${facility.facilityId}${suffix}`,
        linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
      })) ?? []
  );
};
