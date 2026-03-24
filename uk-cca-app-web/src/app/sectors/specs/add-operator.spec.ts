import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { click, getAllByText, getByLabelText, getByTestId, type } from '@testing';

import { CompaniesInformationService } from 'cca-api';

import { SECTORS_ROUTES } from '../sectors.routes';
import { mockAuthState } from './fixtures/mock';
import { navigateToAddOperatorUser, navigateToTargetUnit, navigateToTargetUnitUsers } from './test.utils';

describe('Add operator Spec', () => {
  const accountId = 1;
  const accountName = 'Account_1';
  const sectorId = 123;

  let httpTestingController: HttpTestingController;
  let harness: RouterTestingHarness;
  let authStore: AuthStore;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(SECTORS_ROUTES),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: CompaniesInformationService,
          useValue: {
            getCompanyProfileByRegistrationNumber: () => of(null),
          },
        },
      ],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    authStore = TestBed.inject(AuthStore);
    authStore.setState(mockAuthState);

    await TestBed.compileComponents();
    harness = await RouterTestingHarness.create();
  });

  afterEach(fakeAsync(() => {
    flush();
    httpTestingController.verify();
  }));

  async function setup() {
    const opts = { harness, httpTestingController };

    await navigateToTargetUnit(sectorId, accountName, opts);
    await navigateToTargetUnitUsers(opts, accountId);
    await navigateToAddOperatorUser(opts);
    harness.detectChanges();

    return opts;
  }

  test('Main scenario: Add a new Operator user to the Target Unit account', fakeAsync(async () => {
    const { harness, httpTestingController } = await setup();
    type(getByLabelText('First name') as HTMLInputElement, 'Operator');
    type(getByLabelText('Last name') as HTMLInputElement, 'User');
    type(getByLabelText('Email address') as HTMLInputElement, 'operator_user_1@cca.uk');
    click(getAllByText('Submit')[0]);
    await harness.fixture.whenStable();

    const req = httpTestingController.expectOne(`/api/v1.0/operator-users/invite/account/${accountId}`);
    req.flush(null);
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(getByTestId('confirmation-screen')).toBeTruthy();
  }));

  test('Alternative scenario 4: User does not enter mandatory fields', fakeAsync(async () => {
    await setup();
    type(getByLabelText('First name') as HTMLInputElement, 'Operator');
    click(getAllByText('Submit')[0]);
    harness.detectChanges();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(getAllByText('Enter your last name').length).toBeGreaterThanOrEqual(2);
    expect(getAllByText('Enter your email').length).toBeGreaterThanOrEqual(2);
  }));

  test('Alternative scenario 5: User does not provide valid user email', fakeAsync(async () => {
    await setup();
    type(getByLabelText('First name') as HTMLInputElement, 'Operator');
    type(getByLabelText('Last name') as HTMLInputElement, 'User');
    type(getByLabelText('Email address') as HTMLInputElement, 'operator_user_1');
    click(getAllByText('Submit')[0]);
    harness.detectChanges();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(
      getAllByText('Enter an email address in the correct format, like name@example.com').length,
    ).toBeGreaterThanOrEqual(2);
  }));
});
