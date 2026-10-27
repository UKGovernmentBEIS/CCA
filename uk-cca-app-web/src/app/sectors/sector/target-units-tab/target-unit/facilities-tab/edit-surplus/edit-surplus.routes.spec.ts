import { Location } from '@angular/common';
import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';

import { FacilityInfoDTO, FacilityInfoViewService } from 'cca-api';

import { FACILITIES_LIST_ROUTES } from '../facilities-list.routes';
import { FacilityDetailsComponent } from '../facility-details/facility-details.component';
import FacilitySurplusHistoryComponent from '../surplus-history/surplus-history.component';
import { mockFacilityDetails } from '../testing/mock-data';
import FacilitySurplusConfirmationComponent from './confirmation/confirmation.component';
import EditFacilitySurplusComponent from './edit-surplus.component';

describe('Facility surplus routes', () => {
  let facilityDetails: FacilityInfoDTO;
  let getFacilityDetails: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    facilityDetails = {
      ...mockFacilityDetails,
      facilityBusinessId: 'AIC-F00040',
      surplusDetails: { schemeVersion: 'CCA_3', surplusGained: '1627', hasHistory: true },
    };
    getFacilityDetails = vi.fn(() => of(facilityDetails));
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        provideLocationMocks(),
        provideRouter([{ path: 'facilities', children: FACILITIES_LIST_ROUTES }]),
        { provide: FacilityInfoViewService, useValue: { getFacilityDetailsById: getFacilityDetails } },
      ],
    }).compileComponents();
    TestBed.inject(AuthStore).setUserState({ roleType: 'REGULATOR', status: 'ENABLED' });
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it.each(['REGULATOR', 'OPERATOR', 'SECTOR_USER'] as const)(
    'should allow %s to open facility history',
    async (roleType) => {
      TestBed.inject(AuthStore).setUserState({ roleType, status: 'ENABLED' });
      const harness = await RouterTestingHarness.create();
      await harness.navigateByUrl('/facilities/1/surplus-history', FacilitySurplusHistoryComponent);
      const request = TestBed.inject(HttpTestingController).expectOne((req) =>
        req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/history'),
      );
      request.flush([{ surplusGained: '0', submitter: 'Regulator', submissionDate: '2027-05-01T15:20:00' }]);
      harness.detectChanges();

      expect(getFacilityDetails).toHaveBeenCalledExactlyOnceWith(1);
      expect(harness.routeNativeElement.textContent).toContain('AIC-F00040');
      expect(harness.routeNativeElement.querySelectorAll('govuk-accordion-item')).toHaveLength(1);
    },
  );

  it('should load the new facility history and caption when the component is reused', async () => {
    const harness = await RouterTestingHarness.create();
    const history = await harness.navigateByUrl('/facilities/1/surplus-history', FacilitySurplusHistoryComponent);
    const http = TestBed.inject(HttpTestingController);
    http
      .expectOne((req) => req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/history'))
      .flush([{ surplusGained: '10', submitter: 'First facility submitter' }]);
    harness.detectChanges();

    facilityDetails = { ...facilityDetails, facilityId: 2, facilityBusinessId: 'AIC-F00041' };
    const reused = await harness.navigateByUrl('/facilities/2/surplus-history', FacilitySurplusHistoryComponent);

    expect(reused).toBe(history);
    expect(getFacilityDetails).toHaveBeenLastCalledWith(2);
    expect(harness.routeNativeElement.textContent).toContain('AIC-F00041');
    expect(harness.routeNativeElement.textContent).not.toContain('AIC-F00040');
    expect(harness.routeNativeElement.textContent).not.toContain('First facility submitter');
    http
      .expectOne((req) => req.url.endsWith('/v1.0/facilities/2/buy-out-surplus/history'))
      .flush([{ surplusGained: '20', submitter: 'Second facility submitter' }]);
    harness.detectChanges();
    expect(harness.routeNativeElement.textContent).toContain('Second facility submitter');
  });

  it('should initialise the edit form for another facility after visiting its details', async () => {
    const harness = await RouterTestingHarness.create();
    const edit = await harness.navigateByUrl('/facilities/1/edit-surplus', EditFacilitySurplusComponent);
    edit.form().setValue({ surplus: '2000', comments: 'First facility correction' });
    facilityDetails = {
      ...facilityDetails,
      facilityId: 2,
      facilityBusinessId: 'AIC-F00041',
      surplusDetails: { ...facilityDetails.surplusDetails, surplusGained: '42' },
    };

    await harness.navigateByUrl('/facilities/2', FacilityDetailsComponent);
    const nextEdit = await harness.navigateByUrl('/facilities/2/edit-surplus', EditFacilitySurplusComponent);

    expect(nextEdit).not.toBe(edit);
    expect(getFacilityDetails).toHaveBeenLastCalledWith(2);
    expect(harness.routeNativeElement.querySelector('dd').textContent.trim()).toBe('42');
    expect(nextEdit.form().getRawValue()).toEqual({ surplus: null, comments: '' });
    nextEdit.form().setValue({ surplus: '42', comments: 'Second facility correction' });
    expect(nextEdit.form().controls.surplus.hasError('sameAsCurrent')).toBe(true);
    nextEdit.form().controls.surplus.setValue('1627');
    expect(nextEdit.form().valid).toBe(true);
    nextEdit.onSubmit();
    const http = TestBed.inject(HttpTestingController);
    http.expectOne((req) => req.url.endsWith('/v1.0/requests/workflows')).flush({ requestDetails: [], total: 0 });
    http.expectNone((req) => req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/'));
    const save = http.expectOne((req) => req.url.endsWith('/v1.0/facilities/2/buy-out-surplus/'));
    expect(save.request.method).toBe('POST');
    expect(save.request.body).toEqual({ newSurplusGained: '1627', comments: 'Second facility correction' });
    save.flush(null, { status: 204, statusText: 'No Content' });
    await vi.waitFor(() => expect(TestBed.inject(Router).url).toBe('/facilities/2/edit-surplus/confirmation'));
  });

  it('should update the confirmation return link when the component is reused', async () => {
    const harness = await RouterTestingHarness.create();
    const confirmation = await harness.navigateByUrl(
      '/facilities/1/edit-surplus/confirmation',
      FacilitySurplusConfirmationComponent,
    );
    expect(harness.routeNativeElement.querySelector('a').textContent).toContain('AIC-F00040');

    facilityDetails = { ...facilityDetails, facilityId: 2, facilityBusinessId: 'AIC-F00041' };
    const reused = await harness.navigateByUrl(
      '/facilities/2/edit-surplus/confirmation',
      FacilitySurplusConfirmationComponent,
    );

    expect(reused).toBe(confirmation);
    expect(getFacilityDetails).toHaveBeenLastCalledWith(2);
    expect(harness.routeNativeElement.querySelector('a').textContent).toContain('AIC-F00041');
    expect(harness.routeNativeElement.querySelector('a').textContent).not.toContain('AIC-F00040');
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe('/facilities/2#details');
  });

  it('should reload the saved amount and validation when returning with browser Back', async () => {
    const harness = await RouterTestingHarness.create();
    const router = TestBed.inject(Router);
    router.setUpLocationChangeListener();
    const edit = await harness.navigateByUrl('/facilities/1/edit-surplus', EditFacilitySurplusComponent);
    expect(getFacilityDetails).toHaveBeenCalledWith(1);
    edit.form().setValue({ surplus: '0', comments: 'Correction' });
    edit.onSubmit();
    TestBed.inject(HttpTestingController)
      .expectOne((req) => req.url.endsWith('/v1.0/requests/workflows'))
      .flush({ requestDetails: [], total: 0 });
    const save = TestBed.inject(HttpTestingController).expectOne((req) =>
      req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/'),
    );
    facilityDetails = { ...facilityDetails, surplusDetails: { ...facilityDetails.surplusDetails, surplusGained: '0' } };
    save.flush(null, { status: 204, statusText: 'No Content' });
    await vi.waitFor(() => expect(router.url).toBe('/facilities/1/edit-surplus/confirmation'));
    harness.detectChanges();
    expect(harness.routeNativeElement.textContent).toContain('Surplus gained updated');
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe('/facilities/1#details');

    getFacilityDetails.mockClear();
    TestBed.inject(Location).back();
    await vi.waitFor(() => expect(router.url).toBe('/facilities/1/edit-surplus'));
    harness.detectChanges();
    expect(getFacilityDetails).toHaveBeenCalledExactlyOnceWith(1);
    expect(harness.routeNativeElement.querySelector('dd').textContent.trim()).toBe('0');
    const reopened = harness.routeDebugElement.componentInstance as EditFacilitySurplusComponent;
    reopened.form().setValue({ surplus: '1627', comments: 'Revert the correction' });
    expect(reopened.form().valid).toBe(true);
    reopened.form().controls.surplus.setValue('0');
    expect(reopened.form().controls.surplus.hasError('sameAsCurrent')).toBe(true);
  });

  it.each(['OPERATOR', 'SECTOR_USER'] as const)('should block %s from both edit routes', async (roleType) => {
    TestBed.inject(AuthStore).setUserState({ roleType, status: 'ENABLED' });
    await RouterTestingHarness.create();
    const router = TestBed.inject(Router);
    expect(await router.navigateByUrl('/facilities/1/edit-surplus')).toBe(false);
    expect(await router.navigateByUrl('/facilities/1/edit-surplus/confirmation')).toBe(false);
    expect(getFacilityDetails).not.toHaveBeenCalled();
  });

  describe.each<{ label: string; surplusDetails: FacilityInfoDTO['surplusDetails'] }>([
    { label: 'missing surplus', surplusDetails: undefined },
    { label: 'null surplus', surplusDetails: null },
    { label: 'CCA2 surplus', surplusDetails: { schemeVersion: 'CCA_2', surplusGained: '1627', hasHistory: true } },
    { label: 'unspecified scheme', surplusDetails: { surplusGained: '0' } },
  ])('$label', ({ surplusDetails }) => {
    it.each(['edit-surplus', 'edit-surplus/confirmation', 'surplus-history'])(
      'should redirect direct access to %s to the facility details',
      async (path) => {
        facilityDetails = { ...facilityDetails, surplusDetails };
        const harness = await RouterTestingHarness.create();
        await harness.navigateByUrl(`/facilities/1/${path}`, FacilityDetailsComponent);

        expect(TestBed.inject(Router).url).toBe('/facilities/1#details');
        expect(harness.routeNativeElement.querySelector('form')).toBeNull();
        expect(harness.routeNativeElement.querySelector('a[href*="edit-surplus"]')).toBeNull();
        TestBed.inject(HttpTestingController).expectNone((request) => request.method === 'POST');
        TestBed.inject(HttpTestingController).expectNone((request) => request.url.endsWith('/buy-out-surplus/history'));
      },
    );
  });

  it.each([false, undefined])('should redirect history access when CCA3 hasHistory is %s', async (hasHistory) => {
    facilityDetails = { ...facilityDetails, surplusDetails: { ...facilityDetails.surplusDetails, hasHistory } };
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/facilities/1/surplus-history', FacilityDetailsComponent);

    expect(TestBed.inject(Router).url).toBe('/facilities/1#details');
    expect(harness.routeNativeElement.querySelector('a[href*="surplus-history"]')).toBeNull();
    TestBed.inject(HttpTestingController).expectNone((request) => request.url.endsWith('/buy-out-surplus/history'));
  });

  it('should allow a regulator to edit zero CCA3 surplus without history', async () => {
    facilityDetails = {
      ...facilityDetails,
      surplusDetails: { schemeVersion: 'CCA_3', surplusGained: '0', hasHistory: false },
    };
    const harness = await RouterTestingHarness.create();
    const edit = await harness.navigateByUrl('/facilities/1/edit-surplus', EditFacilitySurplusComponent);

    expect(harness.routeNativeElement.querySelector('dd').textContent.trim()).toBe('0');
    edit.form().setValue({ surplus: '1', comments: 'Correcting the surplus amount' });
    expect(edit.form().valid).toBe(true);
    expect(getFacilityDetails).toHaveBeenCalledExactlyOnceWith(1);
  });
});
