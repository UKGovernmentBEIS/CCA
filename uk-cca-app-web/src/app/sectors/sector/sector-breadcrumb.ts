import { SectorAssociationResponseDTO } from 'cca-api';

type SectorBreadcrumbData = { details: SectorAssociationResponseDTO };

/** Breadcrumb of the sector page. */
export const sectorBreadcrumb = ({ details }: SectorBreadcrumbData) =>
  `${details.sectorAssociationDetails.acronym} - ${details.sectorAssociationDetails.commonName}`;

/**
 * Breadcrumb with the same text as the sector breadcrumb, so it merges into it and makes the sector crumb
 * open the given tab.
 */
export const sectorTabBreadcrumb = (fragment: string) => (data: SectorBreadcrumbData) => ({
  text: sectorBreadcrumb(data),
  fragment,
});
