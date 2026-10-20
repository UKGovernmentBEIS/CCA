import { provideLocationMocks } from '@angular/common/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Route, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { BreadcrumbsComponent, BreadcrumbService } from '@netz/common/navigation';
import { describe, expect, it } from 'vitest';

import { FacilityInfoDTO, FacilityInfoViewService, TargetUnitAccountDetailsResponseDTO } from 'cca-api';

import { FACILITIES_LIST_ROUTES } from '../facilities-list.routes';
import { mockFacilityDetails } from '../testing/mock-data';
import { FACILITY_REPORTS_TAB_ROUTES } from './facility-reports-tab.routes';

const detailsRoutes: Array<[label: string, path: string]> = [
  ['TPR', ''],
  ['PAT', 'pat-details'],
];

const detailsRoute = (path: string): Route => {
  const route = FACILITY_REPORTS_TAB_ROUTES[0].children.find((r) => r.path === path);
  if (!route) throw new Error(`No facility report details route for path "${path}"`);
  return route;
};

const reportsWrapper = (): Route => {
  const route = FACILITIES_LIST_ROUTES[0].children.find((r) => r.path === 'reports');
  if (!route) throw new Error('No reports wrapper route');
  return route;
};

const deepestSnapshot = (snapshot: ActivatedRouteSnapshot): ActivatedRouteSnapshot => {
  let leaf = snapshot;
  while (leaf.firstChild) leaf = leaf.firstChild;
  return leaf;
};

describe('Facility report routes', () => {
  it.each(detailsRoutes)(
    '%s details show the facility as the last breadcrumb instead of a back link',
    (_label, path) => {
      const route = detailsRoute(path);

      expect(route.data.backlink).toBeUndefined();
      expect(route.data.breadcrumb).toBeTypeOf('function');

      const breadcrumb = route.data.breadcrumb as (data: { facilityDetails: FacilityInfoDTO }) => unknown;

      expect(breadcrumb({ facilityDetails: { facilityBusinessId: 'FAC-001' } as FacilityInfoDTO })).toEqual({
        text: 'FAC-001',
        from: ':facilityId',
        fragment: 'reports',
      });
    },
  );

  it.each(detailsRoutes)('%s details resolve the facility they belong to', (_label, path) => {
    expect(detailsRoute(path).resolve.facilityDetails).toBeDefined();
  });

  it('does not suppress breadcrumbs or inherit a back link from the reports wrapper', () => {
    expect(reportsWrapper().data).toBeUndefined();
  });
});

describe('Facility report breadcrumb trail', () => {
  const targetUnit = {
    targetUnitAccountDetails: { name: 'Test target unit' },
  } as TargetUnitAccountDetailsResponseDTO;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideLocationMocks(),
        provideRouter([
          {
            path: ':targetUnitId',
            // Mirrors the target unit crumb and resolve in ACTIVE_TARGET_UNIT_ROUTES.
            data: {
              breadcrumb: ({ targetUnit: unit }: { targetUnit: TargetUnitAccountDetailsResponseDTO }) =>
                unit.targetUnitAccountDetails.name,
            },
            resolve: { targetUnit: () => targetUnit },
            children: [{ path: 'facilities', children: FACILITIES_LIST_ROUTES }],
          },
        ]),
        {
          provide: FacilityInfoViewService,
          useValue: {
            getFacilityDetailsById: () => of({ ...mockFacilityDetails, facilityBusinessId: 'FAC-001' }),
          },
        },
      ],
    }).compileComponents();

    TestBed.createComponent(BreadcrumbsComponent);
  });

  it('keeps the target unit crumb and links the facility crumb to the reports tab', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/1/facilities/2/reports/2024/pat-details');

    expect(TestBed.inject(BreadcrumbService).links()).toEqual([
      { text: 'Test target unit', link: '/1', fragment: 'facilities' },
      { text: 'FAC-001', link: '/1/facilities/2', fragment: 'reports' },
    ]);
  });

  it('does not inherit a back link from the reports wrapper', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/1/facilities/2/reports/2024/pat-details');

    const leaf = deepestSnapshot(TestBed.inject(Router).routerState.snapshot.root);

    expect(leaf.data.backlink).toBeUndefined();
    expect(leaf.data.breadcrumb).toBeTypeOf('function');
  });
});
