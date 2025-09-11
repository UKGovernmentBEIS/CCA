import { DecimalPipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';

import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

export function toChangeAmountSummaryData(txd: BuyOutSurplusTransactionDetailsDTO) {
  const decimalPipe = new DecimalPipe('en-GB');

  return new SummaryFactory()
    .addSection('')
    .addRow('Transaction ID', txd.transactionCode)
    .addRow('Initial amount (GBP)', decimalPipe.transform(txd.invoicedBuyOutFee, '1.2'))
    .addRow('Current amount (GBP)', decimalPipe.transform(txd.buyOutFee, '1.2'))
    .create();
}
