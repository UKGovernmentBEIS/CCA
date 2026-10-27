import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import { mockFacilityTransactionDetailsPageOne } from '../../../testing/cca3-facility-mock-data';
import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import { canActivateCca3ChangeStatus } from './cca3-change-status.guard';

describe('canActivateCca3ChangeStatus', () => {
  const route = { paramMap: convertToParamMap({ transactionId: '1001' }) } as ActivatedRouteSnapshot;

  const runGuard = () =>
    TestBed.runInInjectionContext(() => canActivateCca3ChangeStatus(route, {} as RouterStateSnapshot));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        Cca3TransactionDetailsStore,
        { provide: BuyOutAndSurplusFacilityTransactionViewService, useValue: {} },
      ],
    });
  });

  const [facility] = mockFacilityTransactionDetailsPageOne.transactionDetails;
  const terminated = mockFacilityTransactionDetailsPageOne.transactionDetails.find(
    (detail) => detail.paymentStatus === 'TERMINATED',
  );

  const selectFacilities = (transactionId: number, ...facilities: (typeof facility)[]) => {
    const store = TestBed.inject(Cca3TransactionDetailsStore);
    store.updateState({ transactionId });
    facilities.forEach((selected) => store.updateSelectedFacilities(selected, true));
  };

  const redirectUrl = () => TestBed.inject(Router).serializeUrl(runGuard() as UrlTree);

  it('should allow changing the status when facilities of the same transaction are selected', () => {
    selectFacilities(1001, facility);

    expect(runGuard()).toBe(true);
  });

  it('should redirect to the transaction details when no facility is selected', () => {
    selectFacilities(1001);

    expect(redirectUrl()).toBe('/buyout-surplus/cca3-transactions/1001');
  });

  it('should redirect to the transaction details when only terminated facilities were picked', () => {
    selectFacilities(1001, terminated);

    expect(redirectUrl()).toBe('/buyout-surplus/cca3-transactions/1001');
  });

  it('should redirect to the transaction details when the selection belongs to another transaction', () => {
    selectFacilities(1002, facility);

    expect(redirectUrl()).toBe('/buyout-surplus/cca3-transactions/1001');
  });
});
