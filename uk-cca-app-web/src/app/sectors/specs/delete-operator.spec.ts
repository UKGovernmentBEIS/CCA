import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { transformUsername } from '@netz/common/pipes';
import { click, getByTestId, queryByText } from '@testing';

import { CompaniesInformationService } from 'cca-api';

import { SECTORS_ROUTES } from '../sectors.routes';
import { mockAuthState, mockOperatorAuthorities } from './fixtures/mock';
import { navigateToTargetUnit, navigateToTargetUnitUsers } from './test.utils';

describe('Delete operator spec', () => {
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
    harness.detectChanges();
    return opts;
  }

  test('Main: Delete operator user', fakeAsync(async () => {
    const { harness, httpTestingController } = await setup();
    const firstOperator = mockOperatorAuthorities.authorities[0];
    const deleteControl = Array.from(document.querySelectorAll<HTMLElement>('a,button')).find(
      (el) => el.textContent?.trim() === 'Delete',
    );
    click(deleteControl as HTMLElement);

    let req = httpTestingController.expectOne(`/api/v1.0/operator-users/account/${accountId}/${firstOperator.userId}`);
    req.flush(firstOperator);

    req = httpTestingController.expectOne(`/api/v1.0/operator-authorities/account/${accountId}`);
    req.flush({ ...mockOperatorAuthorities, authorities: mockOperatorAuthorities.authorities.slice(1) });
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(getByTestId('delete-operator-page')).toBeTruthy();
    const confirmRemovalControl = Array.from(document.querySelectorAll<HTMLElement>('a,button')).find(
      (el) => el.textContent?.trim() === 'Confirm removal',
    );
    click(confirmRemovalControl as HTMLElement);

    req = httpTestingController.expectOne(
      `/api/v1.0/operator-authorities/account/${accountId}/${firstOperator.userId}`,
    );
    req.flush(null);

    harness.detectChanges();

    expect(queryByText(transformUsername(firstOperator))).toBeFalsy();
  }));
});
