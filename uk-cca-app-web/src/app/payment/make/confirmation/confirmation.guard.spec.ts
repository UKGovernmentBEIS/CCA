import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ConfirmationGuard } from './confirmation.guard';

describe('ConfirmationGuard', () => {
  let guard: ConfirmationGuard;

  const activatedRouteSnapshotBank = new ActivatedRouteSnapshot();
  activatedRouteSnapshotBank.params = { taskId: 1 };
  activatedRouteSnapshotBank.queryParams = { method: 'BANK_TRANSFER' };

  const activatedRouteSnapshotCredit = new ActivatedRouteSnapshot();
  activatedRouteSnapshotCredit.queryParams = { method: 'CREDIT_OR_DEBIT_CARD' };

  const activatedRouteSnapshotNoQueryParam = new ActivatedRouteSnapshot();
  activatedRouteSnapshotNoQueryParam.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(ConfirmationGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
