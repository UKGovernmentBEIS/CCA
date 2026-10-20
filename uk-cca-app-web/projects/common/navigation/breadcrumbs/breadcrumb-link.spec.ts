import { ActivatedRouteSnapshot, Data, UrlSegment } from '@angular/router';

import { describe, expect, it } from 'vitest';

import { createLink } from './breadcrumb-link';

type RouteStub = { path: string; url?: string; data?: Data };

const segment = (path: string): UrlSegment => ({ toString: () => path }) as unknown as UrlSegment;

/**
 * Minimal ActivatedRouteSnapshot stub. Every node gets its own `pathFromRoot` prefix, like the real snapshots.
 */
const snapshot = (routes: RouteStub[]): ActivatedRouteSnapshot => {
  const nodes = routes.map((route) => ({
    routeConfig: { path: route.path },
    url: [segment(route.url ?? route.path)],
    data: route.data ?? {},
  })) as unknown as ActivatedRouteSnapshot[];

  nodes.forEach((node, index) => {
    (node as { pathFromRoot?: ActivatedRouteSnapshot[] }).pathFromRoot = nodes.slice(0, index + 1);
  });

  return nodes[nodes.length - 1];
};

const facilityChain = (leafData: Data): RouteStub[] => [
  { path: '', url: '' },
  { path: 'target-unit-accounts', url: 'target-unit-accounts' },
  { path: ':targetUnitId', url: '1' },
  { path: 'facilities', url: 'facilities' },
  { path: ':facilityId', url: '2' },
  { path: 'reports', url: 'reports' },
  { path: ':targetPeriodYear', url: '2024' },
  { path: '', url: '', data: leafData },
];

describe('createLink', () => {
  it('links a string breadcrumb to the route itself', () => {
    const link = createLink(
      snapshot([
        { path: 'sectors', url: 'sectors' },
        { path: ':sectorId', url: '5', data: { breadcrumb: 'Sector' } },
      ]),
    );

    expect(link).toEqual({ text: 'Sector', link: '/sectors/5', fragment: undefined });
  });

  it('links an object breadcrumb to the route itself by default', () => {
    const link = createLink(snapshot([{ path: 'login', url: 'login', data: { breadcrumb: { text: 'Login' } } }]));

    expect(link).toEqual({ text: 'Login', link: '/login', fragment: undefined });
  });

  it('resolves from to the nearest ancestor route', () => {
    const link = createLink(
      snapshot(facilityChain({ breadcrumb: { text: 'FAC-001', from: ':facilityId', fragment: 'reports' } })),
    );

    expect(link).toEqual({
      text: 'FAC-001',
      link: '/target-unit-accounts/1/facilities/2',
      fragment: 'reports',
    });
  });

  it('ignores the current route when an ancestor has the same path', () => {
    const link = createLink(
      snapshot([
        { path: ':id', url: '1' },
        { path: 'children', url: 'children' },
        { path: ':id', url: '2', data: { breadcrumb: { text: 'Inner', from: ':id' } } },
      ]),
    );

    expect(link).toEqual({ text: 'Inner', link: '/1', fragment: undefined });
  });

  it('resolves from returned by a breadcrumb function', () => {
    const link = createLink(
      snapshot(
        facilityChain({
          facilityBusinessId: 'FAC-001',
          breadcrumb: ({ facilityBusinessId }: Data) => ({
            text: facilityBusinessId,
            from: ':facilityId',
          }),
        }),
      ),
    );

    expect(link).toEqual({ text: 'FAC-001', link: '/target-unit-accounts/1/facilities/2', fragment: undefined });
  });

  it('prefers an explicit link over from', () => {
    const link = createLink(
      snapshot(facilityChain({ breadcrumb: { text: 'FAC-001', link: '/custom/link', from: ':facilityId' } })),
    );

    expect(link).toEqual({ text: 'FAC-001', link: '/custom/link', fragment: undefined });
  });

  it('falls back to the route itself when from does not match an ancestor', () => {
    const link = createLink(snapshot(facilityChain({ breadcrumb: { text: 'FAC-001', from: ':unknown' } })));

    expect(link).toEqual({
      text: 'FAC-001',
      link: '/target-unit-accounts/1/facilities/2/reports/2024',
      fragment: undefined,
    });
  });

  it('resolves an empty-string from to a pathless ancestor', () => {
    const link = createLink(
      snapshot([
        { path: 'accounts', url: 'accounts' },
        { path: '', url: '' },
        { path: 'reports', url: 'reports', data: { breadcrumb: { text: 'Reports', from: '' } } },
      ]),
    );

    expect(link).toEqual({ text: 'Reports', link: '/accounts', fragment: undefined });
  });

  it('hides the breadcrumb when the breadcrumb function returns false', () => {
    const link = createLink(snapshot(facilityChain({ breadcrumb: () => false })));

    expect(link).toBeNull();
  });

  it('hides the breadcrumb when the breadcrumb function returns null', () => {
    const link = createLink(snapshot(facilityChain({ breadcrumb: (): null => null })));

    expect(link).toBeNull();
  });
});
