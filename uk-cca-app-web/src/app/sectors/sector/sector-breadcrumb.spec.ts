import { SectorAssociationResponseDTO } from 'cca-api';

import { SECTORS_ROUTES } from '../sectors.routes';
import { sectorBreadcrumb, sectorTabBreadcrumb } from './sector-breadcrumb';

describe('sector breadcrumbs', () => {
  const data = {
    details: { sectorAssociationDetails: { acronym: 'ADS', commonName: 'Aerospace' } } as SectorAssociationResponseDTO,
  };

  it('should be the breadcrumb of the sector page', () => {
    const sectorRoute = SECTORS_ROUTES.find((route) => route.path === ':sectorId');

    expect(sectorRoute?.data?.breadcrumb).toBe(sectorBreadcrumb);
    expect(sectorBreadcrumb(data)).toBe('ADS - Aerospace');
  });

  // The breadcrumbs component merges crumbs with the same text, so the tab crumb only takes effect if its text
  // matches the sector crumb exactly.
  it('should build a tab breadcrumb with the sector text and the tab fragment', () => {
    expect(sectorTabBreadcrumb('contacts')(data)).toEqual({ text: sectorBreadcrumb(data), fragment: 'contacts' });
  });
});
