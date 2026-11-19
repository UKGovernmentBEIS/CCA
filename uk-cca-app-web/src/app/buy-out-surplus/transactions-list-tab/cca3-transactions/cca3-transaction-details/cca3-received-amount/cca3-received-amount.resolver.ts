import { inject } from '@angular/core';
import { createUrlTreeFromSnapshot, RedirectCommand, ResolveFn } from '@angular/router';

import {
  BuyOutAndSurplusFacilityTransactionViewService,
  BuyOutSurplusTransactionReceivedAmountHistoryDTO,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

export const cca3ReceivedAmountHistoryResolver: ResolveFn<BuyOutSurplusTransactionReceivedAmountHistoryDTO[]> = (
  route,
) => {
  const transaction = route.parent.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO;

  return transaction.chargeType === 'FEE'
    ? inject(BuyOutAndSurplusFacilityTransactionViewService).getBuyOutSurplusTransactionReceivedAmountHistory(
        transaction.id,
      )
    : new RedirectCommand(createUrlTreeFromSnapshot(route, ['..']));
};
