import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PaymentExistGuard } from './payment-exist.guard';

describe('PaymentExistGuard', () => {
  let guard: PaymentExistGuard;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 500 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(PaymentExistGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
