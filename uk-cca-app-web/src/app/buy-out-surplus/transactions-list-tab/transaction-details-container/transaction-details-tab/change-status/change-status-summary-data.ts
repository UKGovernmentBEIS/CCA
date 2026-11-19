import { SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { AccountBuyOutSurplusTransactionDTO } from 'cca-api';

export function toChangeStatusSummaryData(transactionDetails: AccountBuyOutSurplusTransactionDTO) {
  const statusPipe = new StatusPipe();

  return new SummaryFactory()
    .addSection('')
    .addRow('Transaction ID', transactionDetails?.transactionCode)
    .addRow('Status', statusPipe.transform(transactionDetails?.paymentStatus))
    .create();
}
