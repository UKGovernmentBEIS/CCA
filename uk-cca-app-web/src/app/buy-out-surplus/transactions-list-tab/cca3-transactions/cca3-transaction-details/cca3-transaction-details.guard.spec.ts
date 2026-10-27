import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, Route, RouterStateSnapshot } from '@angular/router';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import { mockFacilityTransactionDetailsPageOne } from '../../testing/cca3-facility-mock-data';
import { canDeactivateCca3TransactionDetails } from './cca3-transaction-details.guard';
import { Cca3TransactionDetailsStore } from './cca3-transaction-details.store';

describe('canDeactivateCca3TransactionDetails', () => {
  const transactionRoute: Route = { path: ':transactionId' };

  const snapshot = (routeConfig: Route | null, transactionId?: string) =>
    ({
      routeConfig,
      paramMap: convertToParamMap(transactionId ? { transactionId } : {}),
      firstChild: null,
    }) as ActivatedRouteSnapshot;

  const currentRoute = snapshot(transactionRoute, '1001');

  // links the snapshots from the root down, the way the router exposes the next state
  const nextStateFor = (...chain: ActivatedRouteSnapshot[]): RouterStateSnapshot => {
    chain.reduce((parent, child) => {
      Object.assign(parent, { firstChild: child });
      return child;
    });

    return { root: chain[0] } as RouterStateSnapshot;
  };

  let store: Cca3TransactionDetailsStore;

  const runGuard = (nextState: RouterStateSnapshot) =>
    TestBed.runInInjectionContext(() =>
      canDeactivateCca3TransactionDetails(null, currentRoute, {} as RouterStateSnapshot, nextState),
    );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        Cca3TransactionDetailsStore,
        { provide: BuyOutAndSurplusFacilityTransactionViewService, useValue: {} },
      ],
    });

    store = TestBed.inject(Cca3TransactionDetailsStore);
    store.updateState({ transactionId: 1001 });
    store.updateSelectedFacilities(mockFacilityTransactionDetailsPageOne.transactionDetails[0], true);
  });

  it('should keep the selection when navigating within the same transaction', () => {
    const nextState = nextStateFor(
      snapshot(null),
      snapshot(transactionRoute, '1001'),
      snapshot({ path: 'change-status' }, '1001'),
    );

    expect(runGuard(nextState)).toBe(true);
    expect(store.state.selectedFacilities.size).toBe(1);
  });

  it('should clear the selection when navigating to another transaction', () => {
    expect(runGuard(nextStateFor(snapshot(null), snapshot(transactionRoute, '1002')))).toBe(true);
    expect(store.state.selectedFacilities.size).toBe(0);
    expect(store.state.transactionId).toBeNull();
  });

  it('should clear the selection when navigating away from the transaction', () => {
    expect(runGuard(nextStateFor(snapshot(null), snapshot({ path: 'buyout-surplus' })))).toBe(true);
    expect(store.state.selectedFacilities.size).toBe(0);
  });
});
