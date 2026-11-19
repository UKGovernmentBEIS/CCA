import { Component, inject } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { FacilityInfoViewService } from 'cca-api';

import { FacilityCertificationResolver } from './facility-details.resolver';
import { mockFacilityCertifications } from './testing/mock-data';

@Component({ template: '' })
class StubComponent {
  readonly data = inject(ActivatedRoute).snapshot.data;
}

describe('FacilityCertificationResolver', () => {
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: ':facilityId',
            children: [
              { path: '', component: StubComponent },
              {
                path: ':certificationPeriod/change-certification-status',
                component: StubComponent,
                resolve: { certificationDetails: FacilityCertificationResolver },
              },
            ],
          },
        ]),
        {
          provide: FacilityInfoViewService,
          useValue: { getFacilityCertifications: () => of(mockFacilityCertifications) },
        },
      ],
    });

    harness = await RouterTestingHarness.create();
  });

  it('should resolve the requested certification period', async () => {
    const component = await harness.navigateByUrl('/1/CP7/change-certification-status', StubComponent);

    expect(component.data.certificationDetails).toEqual(
      mockFacilityCertifications.find((c) => c.certificationPeriod === 'CP7'),
    );
  });

  it('should redirect to the certification status tab when the period is unavailable', async () => {
    await harness.navigateByUrl('/1/CP9/change-certification-status');

    expect(TestBed.inject(Router).url).toBe('/1#certification-status');
  });
});
