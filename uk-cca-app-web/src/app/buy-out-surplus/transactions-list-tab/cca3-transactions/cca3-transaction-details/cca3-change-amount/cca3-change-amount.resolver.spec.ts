import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  provideRouter,
  RedirectCommand,
  RouterStateSnapshot,
} from '@angular/router';

import { firstValueFrom, isObservable, of } from 'rxjs';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import { cca3ChangeAmountFacilityResolver } from './cca3-change-amount.resolver';

describe('CCA3 amount facility resolver', () => {
  const facility = { id: 42, facilityBusinessId: 'ADS_1-F00005', paymentStatus: 'AWAITING_PAYMENT' } as const;
  const getDetails = vi.fn();
  const resolve = async (facilityBusinessId: string = facility.facilityBusinessId) => {
    const result = TestBed.runInInjectionContext(() =>
      cca3ChangeAmountFacilityResolver(
        {
          paramMap: convertToParamMap({ transactionId: '10', facilityBusinessId }),
        } as ActivatedRouteSnapshot,
        {} as RouterStateSnapshot,
      ),
    );
    return isObservable(result) ? firstValueFrom(result) : result;
  };

  beforeEach(() => {
    getDetails.mockReset();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: BuyOutAndSurplusFacilityTransactionViewService,
          useValue: { getFacilityBuyOutSurplusTransactionDetails: getDetails },
        },
      ],
    });
  });

  it('uses the exact facility in the transaction, including for a direct link', async () => {
    getDetails.mockReturnValue(
      of({ total: 2, transactionDetails: [{ ...facility, id: 99, facilityBusinessId: 'ADS_1-F000050' }, facility] }),
    );
    expect(await resolve()).toEqual(facility);
    expect(getDetails).toHaveBeenCalledWith(10, { term: facility.facilityBusinessId, pageNumber: 0, pageSize: 50 });
  });

  it('continues past matching site names to an exact facility on another page', async () => {
    getDetails
      .mockReturnValueOnce(
        of({
          total: 51,
          transactionDetails: Array.from({ length: 50 }, (_, id) => ({
            id,
            facilityBusinessId: `OTHER-${id}`,
            siteName: facility.facilityBusinessId,
          })),
        }),
      )
      .mockReturnValueOnce(of({ total: 51, transactionDetails: [facility] }));
    expect(await resolve()).toEqual(facility);
    expect(getDetails).toHaveBeenLastCalledWith(10, { term: facility.facilityBusinessId, pageNumber: 1, pageSize: 50 });
  });

  it('redirects instead of editing an unrelated facility', async () => {
    getDetails.mockReturnValue(of({ total: 1, transactionDetails: [{ ...facility, facilityBusinessId: 'OTHER' }] }));
    expect(await resolve()).toBeInstanceOf(RedirectCommand);
  });

  it('redirects when no exact facility exists on any of the result pages', async () => {
    getDetails
      .mockReturnValueOnce(
        of({
          total: 51,
          transactionDetails: Array.from({ length: 50 }, (_, id) => ({ id, facilityBusinessId: `OTHER-${id}` })),
        }),
      )
      .mockReturnValueOnce(of({ total: 51, transactionDetails: [{ ...facility, facilityBusinessId: 'OTHER-50' }] }));
    expect(await resolve()).toBeInstanceOf(RedirectCommand);
    expect(getDetails).toHaveBeenCalledTimes(2);
  });

  it('stops on an empty page even if the reported total suggests more results', async () => {
    getDetails.mockReturnValue(of({ total: 100, transactionDetails: [] }));
    expect(await resolve()).toBeInstanceOf(RedirectCommand);
    expect(getDetails).toHaveBeenCalledTimes(1);
  });

  it.each([null, 'AB', 'A'.repeat(256)])(
    'redirects an invalid facility ID without querying the API: %s',
    async (id) => {
      expect(await resolve(id)).toBeInstanceOf(RedirectCommand);
      expect(getDetails).not.toHaveBeenCalled();
    },
  );
});
