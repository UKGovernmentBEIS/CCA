import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';

import { Observable, of, switchMap } from 'rxjs';

import {
  BuyOutAndSurplusFacilityTransactionViewService,
  FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO,
} from 'cca-api';

export const cca3ChangeAmountFacilityResolver: ResolveFn<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO> = (
  route,
) => {
  const facilityBusinessId = route.paramMap.get('facilityBusinessId');
  const transactionId = Number(route.paramMap.get('transactionId'));
  const router = inject(Router);

  if (!facilityBusinessId || facilityBusinessId.length < 3 || facilityBusinessId.length > 255) {
    return new RedirectCommand(router.createUrlTree(['/buyout-surplus/cca3-transactions', transactionId]));
  }

  const api = inject(BuyOutAndSurplusFacilityTransactionViewService);
  const pageSize = 50;
  const findFacility = (
    pageNumber: number,
  ): Observable<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO | RedirectCommand> =>
    api
      .getFacilityBuyOutSurplusTransactionDetails(transactionId, { term: facilityBusinessId, pageNumber, pageSize })
      .pipe(
        switchMap(({ transactionDetails, total }) => {
          // The API also matches site names and partial IDs, so find the exact facility.
          const facility = transactionDetails?.find((detail) => detail.facilityBusinessId === facilityBusinessId);
          if (facility) return of(facility);

          if (transactionDetails?.length && (pageNumber + 1) * pageSize < total) {
            return findFacility(pageNumber + 1);
          }

          return of(new RedirectCommand(router.createUrlTree(['/buyout-surplus/cca3-transactions', transactionId])));
        }),
      );

  return findFacility(0);
};
