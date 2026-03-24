import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { transformUsername } from '@netz/common/pipes';
import { click, getByText } from '@testing';

import { SECTORS_ROUTES } from '../sectors.routes';
import { mockSectorAuthorities } from './fixtures/mock';
import { navigateToTargetUnits } from './test.utils';

describe('Target Units List Spec', () => {
  let httpTestingController: HttpTestingController;
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(SECTORS_ROUTES), provideHttpClient(), provideHttpClientTesting()],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    await TestBed.compileComponents();
    harness = await RouterTestingHarness.create();
  });

  afterEach(fakeAsync(() => {
    flush();
    httpTestingController.verify();
  }));

  test("Main: assign a site contact to an 'Unassigned' Target Unit", fakeAsync(async () => {
    const sectorId = 123;
    const sectorUser = mockSectorAuthorities.authorities[0];
    const opts = { harness, httpTestingController };

    await navigateToTargetUnits(sectorId, opts);

    const sectorUserId = sectorUser.userId;
    const select = document.getElementById('targetUnits.1.assignedTo') as HTMLSelectElement;
    select.value = `1: ${sectorUserId}`;
    select.dispatchEvent(new Event('change', { bubbles: true }));
    click(getByText('Save'));

    const req = httpTestingController.expectOne(`/api/v1.0/sector-association/${sectorId}/target-unit-accounts/`);
    req.flush(null);
    expect(select.selectedOptions[0]?.textContent ?? '').toMatch(new RegExp(transformUsername(sectorUser)));
  }));

  test('Alternative scenario 1: Un-assign a site contact from a Target Unit', fakeAsync(async () => {
    const sectorId = 123;
    const opts = { harness, httpTestingController };

    await navigateToTargetUnits(sectorId, opts);

    const select = document.getElementById('targetUnits.0.assignedTo') as HTMLSelectElement;
    select.value = '0: null';
    select.dispatchEvent(new Event('change', { bubbles: true }));
    click(getByText('Save'));

    const req = httpTestingController.expectOne(`/api/v1.0/sector-association/${sectorId}/target-unit-accounts/`);
    req.flush(null);
    expect(select.selectedOptions[0]?.textContent ?? '').toMatch(/Unassigned/);
  }));
});
