import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BankTransferGuard } from './bank-transfer.guard';

describe('BankTransferGuard', () => {
  let guard: BankTransferGuard;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(BankTransferGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
