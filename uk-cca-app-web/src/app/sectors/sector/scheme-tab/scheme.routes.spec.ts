import { SCHEME_ROUTES } from './scheme.routes';
import { SubsectorAssociationSchemeResolver } from './subsector-scheme.resolver';

describe('SCHEME_ROUTES', () => {
  const subsectorWrapper = SCHEME_ROUTES.find((route) => route.path === 'subsector');
  const subsectorRoute = subsectorWrapper?.children?.find((route) => route.path === ':subId');

  it('should refresh subsector scheme data when navigating between subsector child routes', () => {
    expect(subsectorRoute?.resolve).toEqual({ subSector: SubsectorAssociationSchemeResolver });
    expect(subsectorRoute?.runGuardsAndResolvers).toBe('always');
  });

  it('should link the sector breadcrumb of subsector pages to the scheme tab', () => {
    const breadcrumb = subsectorWrapper?.data?.breadcrumb as (data: unknown) => { fragment: string };

    expect(
      breadcrumb({ details: { sectorAssociationDetails: { acronym: 'ADS', commonName: 'Aerospace' } } }).fragment,
    ).toBe('scheme');
  });
});
