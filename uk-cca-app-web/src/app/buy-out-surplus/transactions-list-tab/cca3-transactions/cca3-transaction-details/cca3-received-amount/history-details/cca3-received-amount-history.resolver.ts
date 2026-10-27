import { createUrlTreeFromSnapshot, RedirectCommand, ResolveFn } from '@angular/router';

import { BuyOutSurplusTransactionReceivedAmountHistoryDTO } from 'cca-api';

export const cca3ReceivedAmountHistoryEntryResolver: ResolveFn<BuyOutSurplusTransactionReceivedAmountHistoryDTO> = (
  route,
) => {
  const history = route.parent.data.receivedAmountHistory as BuyOutSurplusTransactionReceivedAmountHistoryDTO[];

  return (
    history.find((entry) => entry.id === +route.paramMap.get('detailsId')) ??
    new RedirectCommand(createUrlTreeFromSnapshot(route, ['../..']))
  );
};
