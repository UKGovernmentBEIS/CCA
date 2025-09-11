import { SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

export function toChangeStatusSummaryData(transactionDetails: BuyOutSurplusTransactionDetailsDTO) {
  const statusPipe = new StatusPipe();

  return new SummaryFactory()
    .addSection('')
    .addRow('Transaction ID', transactionDetails?.transactionCode)
    .addRow('Status', statusPipe.transform(transactionDetails?.paymentStatus))
    .create();
}
