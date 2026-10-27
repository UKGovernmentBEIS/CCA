import { TargetUnitAccountDetailsResponseDTO } from 'cca-api';

import { ACTIVE_TARGET_UNIT_ROUTES } from './target-unit.routes';
import { targetUnitBreadcrumb, targetUnitTabBreadcrumb } from './target-unit-breadcrumb';

describe('target unit breadcrumbs', () => {
  const data = {
    targetUnit: { targetUnitAccountDetails: { name: 'Test target unit' } } as TargetUnitAccountDetailsResponseDTO,
  };

  it('should be the breadcrumb of the target unit page', () => {
    const targetUnitRoute = ACTIVE_TARGET_UNIT_ROUTES.find((route) => route.path === ':targetUnitId');

    expect(targetUnitRoute?.data?.breadcrumb).toBe(targetUnitBreadcrumb);
    expect(targetUnitBreadcrumb(data)).toBe('Test target unit');
  });

  // The breadcrumbs component merges crumbs with the same text, so the tab crumb only takes effect if its text
  // matches the target unit crumb exactly.
  it('should build a tab breadcrumb with the target unit text and the tab fragment', () => {
    expect(targetUnitTabBreadcrumb('reports')(data)).toEqual({ text: targetUnitBreadcrumb(data), fragment: 'reports' });
  });
});
