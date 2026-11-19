import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

import { AuthStore } from '@netz/common/auth';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import { canActivateCca3MarkAllFacilities } from './cca3-mark-all-facilities.guard';

describe('canActivateCca3MarkAllFacilities', () => {
  const route = { paramMap: convertToParamMap({ transactionId: '1001' }) } as ActivatedRouteSnapshot;

  const runGuard = () =>
    TestBed.runInInjectionContext(() => canActivateCca3MarkAllFacilities(route, {} as RouterStateSnapshot));

  function setup(roleType: 'REGULATOR' | 'OPERATOR', storeTransactionId: number | null) {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        Cca3TransactionDetailsStore,
        { provide: BuyOutAndSurplusFacilityTransactionViewService, useValue: {} },
      ],
    });

    TestBed.inject(AuthStore).setUserState({ roleType });
    TestBed.inject(Cca3TransactionDetailsStore).updateState({ transactionId: storeTransactionId });
  }

  const redirectUrl = () => TestBed.inject(Router).serializeUrl(runGuard() as UrlTree);

  it('should allow regulators coming from the details page of the same transaction', () => {
    setup('REGULATOR', 1001);

    expect(runGuard()).toBe(true);
  });

  it('should redirect to the transaction details on a page refresh or a URL entered by hand', () => {
    setup('REGULATOR', null);

    expect(redirectUrl()).toBe('/buyout-surplus/cca3-transactions/1001');
  });

  it('should redirect to the transaction details when the store belongs to another transaction', () => {
    setup('REGULATOR', 1002);

    expect(redirectUrl()).toBe('/buyout-surplus/cca3-transactions/1001');
  });

  it('should redirect to the transaction details when the user is not a regulator', () => {
    setup('OPERATOR', 1001);

    expect(redirectUrl()).toBe('/buyout-surplus/cca3-transactions/1001');
  });
});
