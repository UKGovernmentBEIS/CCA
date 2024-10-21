/**
 * this is a bad test suite. It's too rigid, has many static dependencies. If it proves to get in the way,
 * either remove or rewrite it`
 */
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { transformUsername } from '@netz/common/pipes';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { SECTORS_ROUTES } from '../sectors.routes';
import { mockSectorAuthorities, mockSectorUserDetails } from './fixtures/mock';
import {
  changeSectorUserDetailsFormName,
  checkThatSectorUserDetailsAreUpdated,
  navigateToContacts,
  navigateToEditSectorUserDetails,
  navigateToSectorUserDetails,
  submitSectorUserDetailsForm,
} from './test.utils';

describe('Edit sector user spec', () => {
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

  test('Main: Administrator views and edits user details of another user', async () => {
    const sectorId = 123;
    const sectorUser = mockSectorAuthorities.authorities[0];
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(sectorId, opts);
    await navigateToSectorUserDetails(sectorId, sectorUser.userId, transformUsername(sectorUser), opts);
    await navigateToEditSectorUserDetails(opts);
    await changeSectorUserDetailsFormName('reg1', 'basic1', opts);
    await submitSectorUserDetailsForm(opts);

    let req = httpTestingController.expectOne(
      `/api/v1.0/sector-users/sector-association/${sectorId}/${sectorUser.userId}`,
    );
    req.flush({ ...mockSectorUserDetails, firstName: 'reg1', lastName: 'basic1' });
    await harness.fixture.whenStable();
    harness.detectChanges();

    req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush({
      editable: true,
      authorities: mockSectorAuthorities.authorities.map((s, idx) => {
        if (idx === 0) {
          return { ...s, firstName: 'reg1', lastName: 'basic1' };
        }
        return s;
      }),
    });
    await harness.fixture.whenStable();
    harness.detectChanges();
    await checkThatSectorUserDetailsAreUpdated('reg1 basic1');
  });

  test('Alternative scenario 1: User views and edits his own user details', async () => {
    const sectorId = 123;
    const sectorUser = mockSectorAuthorities.authorities[1];
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(sectorId, opts);
    await navigateToSectorUserDetails(sectorId, sectorUser.userId, transformUsername(sectorUser), opts);
    await navigateToEditSectorUserDetails(opts);
    await changeSectorUserDetailsFormName('reg3', 'admin3', opts);
    await submitSectorUserDetailsForm(opts);

    let req = httpTestingController.expectOne(
      `/api/v1.0/sector-users/sector-association/${sectorId}/${sectorUser.userId}`,
    );
    req.flush({ ...mockSectorUserDetails, firstName: 'reg3', lastName: 'basic3' });

    await harness.fixture.whenStable();
    harness.detectChanges();

    req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush({
      editable: true,
      authorities: mockSectorAuthorities.authorities.map((s, idx) => {
        if (idx === 1) {
          return { ...s, firstName: 'reg3', lastName: 'basic3' };
        }
        return s;
      }),
    });

    harness.detectChanges();
    await checkThatSectorUserDetailsAreUpdated('reg3 basic3');
  });

  test("Alternative scenario 2: Basic user clicks the 'Change two factor authentication' link", async () => {
    const sectorId = 123;
    const sectorUser = mockSectorAuthorities.authorities[0];
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };
    await navigateToContacts(sectorId, opts);
    await navigateToSectorUserDetails(sectorId, sectorUser.userId, transformUsername(sectorUser), opts);

    expect(screen.getByText('Reset two-factor authentication')).toHaveAttribute('href', '/2fa/reset-2fa');
  });

  test("Alternative scenario 3: Administrator clicks the 'Change two factor authentication' link", async () => {
    const sectorId = 123;
    const sectorUser = mockSectorAuthorities.authorities[1];
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(sectorId, opts);
    await navigateToSectorUserDetails(sectorId, sectorUser.userId, transformUsername(sectorUser), opts);

    expect(screen.getByText('Reset two-factor authentication')).toHaveAttribute('href', '/2fa/reset-2fa');
  });
});
