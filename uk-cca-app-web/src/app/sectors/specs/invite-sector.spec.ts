import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed, tick } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { clear, click, getAllByText, getByLabelText, getByTestId, getByText, type } from '@testing';

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
    expect(getByTestId('sector-list')).toBeTruthy();
  }));

  test('Main Scenario: add a new Sector User to a Sector', fakeAsync(async () => {
    const id = 231;
    const opts = { harness, httpTestingController };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await fillAddSectorUserForm('sector_user@cca.uk');
    await submitSectorUserForm(opts);

    await harness.fixture.whenStable();
    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(null);

    await harness.fixture.whenStable();
    await assertConfirmationPage();

    const link = getByText('Return to: Contacts');
    click(link);
    harness.detectChanges();
    const req2 = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
    req2.flush(mockSectorAuthorities);
    harness.detectChanges();
    expect(getByTestId('sector-user-type-form')).toBeTruthy();
  }));

  test('Alternative Scenario 1: User provides an existing user’s email for this Sector', fakeAsync(async () => {
    const id = 231;
    const opts = { harness, httpTestingController };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await fillAddSectorUserForm('sector_user@cca.uk');
    await submitSectorUserForm(opts);

    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(duplicateEmailResponse, { status: 400, statusText: '' });
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(getAllByText('This user email already exists in CCA for this Sector').length).toBeGreaterThanOrEqual(2);
  }));

  // Alternative Scenario 2: User provides an existing (other Sector) user’s email
  // this is handled by the backend. Nothing to test here
  test('Alternative Scenario 3: User does not enter mandatory fields', fakeAsync(async () => {
    const id = 231;
    const opts = { harness, httpTestingController };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await submitSectorUserForm(opts);

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(getAllByText(/first name/i).length).toBeGreaterThan(0);
    expect(getAllByText(/last name/i).length).toBeGreaterThan(0);
    expect(getAllByText(/email/i).length).toBeGreaterThan(0);

    await fillAddSectorUserForm('sector_user@cca.uk');
    await submitSectorUserForm(opts);
    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(null);
    await harness.fixture.whenStable();
    await assertConfirmationPage();
  }));

  test('Alternative Scenario 4: User does not provide valid user email', fakeAsync(async () => {
    const id = 231;
    const opts = { harness, httpTestingController };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    const emailInput = getByLabelText('Email address') as HTMLInputElement;
    type(emailInput, 'invalid email');
    await submitSectorUserForm(opts);
    expect(getAllByText('Enter an email address in the correct format, like name@example.com')).toHaveLength(2);

    clear(getByLabelText('First name') as HTMLInputElement);
    clear(getByLabelText('Last name') as HTMLInputElement);
    clear(emailInput);
    await fillAddSectorUserForm('sector_user@cca.uk');
    await submitSectorUserForm(opts);

    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(null);
    await harness.fixture.whenStable();
    await assertConfirmationPage();
  }));

  test('Alternative Scenario 5: User provides an existing (Regulator or Operator) user’s email', fakeAsync(async () => {
    const id = 231;
    const opts = { harness, httpTestingController };

    await navigateToContacts(id, opts);
    await navigateToAddSector(id, '0: sector_user_administrator', opts);
    await fillAddSectorUserForm('sector_user@cca.uk');
    await submitSectorUserForm(opts);

    const req = httpTestingController.expectOne(`/api/v1.0/sector-users/invite/sector-association/${id}`);
    req.flush(duplicateEmailResponse, { status: 400, statusText: '' });
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(getAllByText('This user email already exists in CCA for this Sector').length).toBeGreaterThanOrEqual(2);
  }));
});
