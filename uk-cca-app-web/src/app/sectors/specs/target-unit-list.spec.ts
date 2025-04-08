import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { transformUsername } from '@netz/common/pipes';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

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
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToTargetUnits(sectorId, opts);

    const sectorUserId = sectorUser.userId;
    const select = document.getElementById('targetUnits.1.assignedTo');
    await user.selectOptions(select, `1: ${sectorUserId}`);
    await user.click(screen.getByText('Save'));

    const req = httpTestingController.expectOne(`/api/v1.0/account-site-contacts/sector-association/${sectorId}`);
    req.flush(null);
    expect(select).toHaveDisplayValue(new RegExp(transformUsername(sectorUser)));
  }));

  test('Alternative scenario 1: Un-assign a site contact from a Target Unit', fakeAsync(async () => {
    const sectorId = 123;
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToTargetUnits(sectorId, opts);

    const select = document.getElementById('targetUnits.0.assignedTo');
    await user.selectOptions(select, `0: null`);
    await user.click(screen.getByText('Save'));

    const req = httpTestingController.expectOne(`/api/v1.0/account-site-contacts/sector-association/${sectorId}`);
    req.flush(null);
    expect(select).toHaveDisplayValue(/Unassigned/);
  }));
});
