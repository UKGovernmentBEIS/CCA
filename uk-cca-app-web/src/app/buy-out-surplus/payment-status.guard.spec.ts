import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { paymentStatusRedirectGuard } from './payment-status.guard';

@Component({
  template: '',
})
class TestComponent {}

describe('paymentStatusRedirectGuard', () => {
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: 'buyout-surplus',
            component: TestComponent,
            canActivate: [paymentStatusRedirectGuard],
          },
        ]),
      ],
    });

    router = TestBed.inject(Router);
  });

  it('preserves the fragment when adding the default payment status', async () => {
    await router.navigateByUrl('/buyout-surplus#cost');

    expect(router.url).toBe('/buyout-surplus?paymentStatus=AWAITING_PAYMENT#cost');
  });

  it('preserves an explicitly selected payment status', async () => {
    await router.navigateByUrl('/buyout-surplus?paymentStatus=PAID#transactions');

    expect(router.url).toBe('/buyout-surplus?paymentStatus=PAID#transactions');
  });
});
