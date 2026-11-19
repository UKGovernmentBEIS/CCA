import { provideLocationMocks } from '@angular/common/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Route, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { BreadcrumbsComponent, BreadcrumbService } from '@netz/common/navigation';
import { describe, expect, it } from 'vitest';

import {
  FacilityInfoDTO,
  FacilityInfoViewService,
  TargetPeriodPerformanceDataReportOfTheFacilityService,
  TargetUnitAccountDetailsResponseDTO,
} from 'cca-api';

import { targetUnitBreadcrumb } from '../../../target-unit-breadcrumb';
import { FACILITIES_LIST_ROUTES } from '../facilities-list.routes';
import { mockFacilityDetails } from '../testing/mock-data';
import { FACILITY_REPORTS_TAB_ROUTES } from './facility-reports-tab.routes';

const targetPeriodYearRoute = (): Route => FACILITY_REPORTS_TAB_ROUTES[0];

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
  it('give every report page a facility breadcrumb that links to the reports tab', () => {
    const route = targetPeriodYearRoute();

    expect(route.data.backlink).toBeUndefined();
    expect(route.data.breadcrumb).toBeTypeOf('function');

    const breadcrumb = route.data.breadcrumb as (data: { facilityDetails: FacilityInfoDTO }) => unknown;

    expect(breadcrumb({ facilityDetails: { facilityBusinessId: 'FAC-001' } as FacilityInfoDTO })).toEqual({
      text: 'FAC-001',
      from: ':facilityId',
      fragment: 'reports',
    });
  });

  it('resolve the facility the reports belong to', () => {
    expect(targetPeriodYearRoute().resolve.facilityDetails).toBeDefined();
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
            data: { breadcrumb: targetUnitBreadcrumb },
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
        {
          provide: TargetPeriodPerformanceDataReportOfTheFacilityService,
          useValue: { getFacilityPerformanceDataReportDetails: () => of({}) },
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

  // Products renders from a store that the TP report page fills, so navigate without an outlet to
  // check only the trail.
  it('links products back to the TP report', async () => {
    await TestBed.inject(Router).navigateByUrl('/1/facilities/2/reports/2024/products');

    expect(TestBed.inject(BreadcrumbService).links()).toEqual([
      { text: 'Test target unit', link: '/1', fragment: 'facilities' },
      { text: 'FAC-001', link: '/1/facilities/2', fragment: 'reports' },
      { text: 'TP report results', link: '/1/facilities/2/reports/2024', fragment: undefined },
    ]);
  });
});
