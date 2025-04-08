import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { AuthStore } from '@netz/common/auth';
import { transformUsername } from '@netz/common/pipes';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

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
      providers: [provideRouter(SECTORS_ROUTES), provideHttpClient(), provideHttpClientTesting()],
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
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToTargetUnit(sectorId, accountName, opts);
    await navigateToTargetUnitUsers(opts, accountId);
    harness.detectChanges();
    return opts;
  }

  test('Main: Delete operator user', fakeAsync(async () => {
    const { user, harness, httpTestingController } = await setup();
    const firstOperator = mockOperatorAuthorities.authorities[0];
    await user.click(screen.getAllByText('Delete')[0]);

    let req = httpTestingController.expectOne(`/api/v1.0/operator-users/account/${accountId}/${firstOperator.userId}`);
    req.flush(firstOperator);

    req = httpTestingController.expectOne(`/api/v1.0/operator-authorities/account/${accountId}`);
    req.flush({ ...mockOperatorAuthorities, authorities: mockOperatorAuthorities.authorities.slice(1) });
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(screen.getByTestId('delete-operator-page')).toBeInTheDocument();
    await user.click(screen.getByText('Confirm removal'));

    req = httpTestingController.expectOne(
      `/api/v1.0/operator-authorities/account/${accountId}/${firstOperator.userId}`,
    );
    req.flush(null);

    harness.detectChanges();

    expect(screen.queryByText(transformUsername(firstOperator))).not.toBeInTheDocument();
  }));
});
