import { SCHEME_ROUTES } from './scheme.routes';
import { SubsectorAssociationSchemeResolver } from './subsector-scheme.resolver';

describe('SCHEME_ROUTES', () => {
  it('should refresh subsector scheme data when navigating between subsector child routes', () => {
    const subsectorRoute = SCHEME_ROUTES.find((route) => route.path === 'subsector/:subId');

    expect(subsectorRoute?.resolve).toEqual({ subSector: SubsectorAssociationSchemeResolver });
    expect(subsectorRoute?.runGuardsAndResolvers).toBe('always');
  });
});
