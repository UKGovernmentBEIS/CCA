import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed, tick } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { SECTORS_ROUTES } from '../sectors.routes';
import { SectorListComponent } from '../sectors-list/sector-list.component';
import { duplicateEmailResponse, mockSectorAuthorities } from './fixtures/mock';
import { sectorListFixture } from './fixtures/sector-list.fixture';
import {
  assertConfirmationPage,
  fillAddSectorUserForm,
  navigateToAddSector,
  navigateToContacts,
  submitSectorUserForm,
} from './test.utils';

describe('Invite Sector Spec', () => {
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

  test('smoke test', fakeAsync(async () => {
    harness.navigateByUrl('/', SectorListComponent);
    tick();

    const req = httpTestingController.expectOne('/api/v1.0/sector-association/');
    req.flush(sectorListFixture);
    harness.detectChanges();

    const location = TestBed.inject(Location);
    expect(location.path()).toEqual('');
    expect(screen.getByTestId('sector-list')).toBeInTheDocument();
  }));

  test('Main Scenario: add a new Sector User to a Sector', fakeAsync(async () => {
    const id = 231;
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await fillAddSectorUserForm('sector_user@cca.uk', opts);
    await submitSectorUserForm(opts);

    await harness.fixture.whenStable();
    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(null);

    await harness.fixture.whenStable();
    await assertConfirmationPage();

    const link = screen.getByText('Return to: Contacts');
    await user.click(link);
    harness.detectChanges();
    const req2 = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
    req2.flush(mockSectorAuthorities);
    harness.detectChanges();
    expect(screen.getByTestId('sector-user-type-form')).toBeVisible();
  }));
  test('Alternative Scenario 1: User provides an existing user’s email for this Sector', fakeAsync(async () => {
    const id = 231;
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await fillAddSectorUserForm('sector_user@cca.uk', opts);
    await submitSectorUserForm(opts);

    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(duplicateEmailResponse, { status: 400, statusText: '' });
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeInTheDocument();
    expect(screen.getAllByText('This user email already exists in CCA for this Sector')).toHaveLength(2);
  }));

  // Alternative Scenario 2: User provides an existing (other Sector) user’s email
  // this is handled by the backend. Nothing to test here
  test('Alternative Scenario 3: User does not enter mandatory fields', fakeAsync(async () => {
    const id = 231;
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await submitSectorUserForm(opts);

    expect(document.querySelector('.govuk-error-summary')).toBeInTheDocument();
    expect(screen.getAllByText('Enter the user’s first name')).toHaveLength(2);
    expect(screen.getAllByText('Enter the user’s last name')).toHaveLength(2);
    expect(screen.getAllByText('Enter the user’s email')).toHaveLength(2);

    await fillAddSectorUserForm('sector_user@cca.uk', opts);
    await submitSectorUserForm(opts);
    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(null);
    await harness.fixture.whenStable();
    await assertConfirmationPage();
  }));

  test('Alternative Scenario 4: User does not provide valid user email', fakeAsync(async () => {
    const id = 231;
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await user.type(screen.getByLabelText('Email address'), 'invalid email');
    await submitSectorUserForm(opts);
    expect(screen.getAllByText('Enter an email address in the correct format, like name@example.com')).toHaveLength(2);

    await user.clear(screen.getByLabelText('First name'));
    await user.clear(screen.getByLabelText('Last name'));
    await user.clear(screen.getByLabelText('Email address'));
    await fillAddSectorUserForm('sector_user@cca.uk', opts);
    await submitSectorUserForm(opts);

    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(null);
    await harness.fixture.whenStable();
    await assertConfirmationPage();
  }));

  test('Alternative Scenario 5: User provides an existing (Regulator or Operator) user’s email', fakeAsync(async () => {
    const id = 231;
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await fillAddSectorUserForm('sector_user@cca.uk', opts);
    await submitSectorUserForm(opts);

    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(duplicateEmailResponse, { status: 400, statusText: '' });
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeInTheDocument();
    expect(screen.getAllByText('This user email already exists in CCA for this Sector')).toHaveLength(2);
  }));
});
