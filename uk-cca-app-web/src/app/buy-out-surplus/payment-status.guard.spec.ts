import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { paymentStatusRedirectGuard } from './payment-status.guard';

@Component({ template: '' })
class TestComponent {}

describe('paymentStatusRedirectGuard', () => {
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: 'buyout-surplus',
            canActivate: [paymentStatusRedirectGuard],
            component: TestComponent,
          },
        ]),
      ],
    });

    router = TestBed.inject(Router);
  });

  it('preserves the fragment when adding the default payment status', async () => {
    await router.navigateByUrl('/buyout-surplus#cost');

    expect(router.url).toBe('/buyout-surplus?buyOutSurplusPaymentStatus=AWAITING_PAYMENT#cost');
  });
});
