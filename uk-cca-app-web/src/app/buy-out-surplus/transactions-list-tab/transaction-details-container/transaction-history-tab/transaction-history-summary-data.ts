import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import {
  BuyOutSurplusTransactionHistoryDTO,
  BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload,
} from 'cca-api';
import { BuyOutSurplusTransactionAmountChangedHistoryPayload } from 'cca-api';

export function toStatusHistorySummaryData(transactionHistoryDTO: BuyOutSurplusTransactionHistoryDTO): SummaryData {
  const summaryFactory = new SummaryFactory();

  const th = transactionHistoryDTO.payload as BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload;
  const statusesPipe = new StatusPipe();
  const datePipe = new GovukDatePipe();

  summaryFactory.addSection('', '').addRow('Status', statusesPipe.transform(th.paymentStatus));

  if (th.paymentStatus !== 'TERMINATED') {
    if (th.paymentStatus === 'PAID') {
      summaryFactory.addRow(
        'Date when the payment was received in the bank account',
        th?.paymentDate ? datePipe.transform(th.paymentDate, 'date') : 'Not provided',
      );
    }

    summaryFactory
      .addRow('Comments', th.comments ? th.comments : 'Not provided')
      .addFileListRow(
        'Uploaded evidence',
        fileUtils.toDownloadableFiles(th.evidenceFiles, 'file-evidences-download/document'),
      );
  }

  return summaryFactory.create();
}

export function toAmountHistorySummaryData(transactionHistoryDTO: BuyOutSurplusTransactionHistoryDTO): SummaryData {
  const th = transactionHistoryDTO.payload as BuyOutSurplusTransactionAmountChangedHistoryPayload;
  const decimalPipe = new DecimalPipe('en-GB');

  return new SummaryFactory()
    .addSection('', '')
    .addRow('Amount', decimalPipe.transform(th.amount))
    .addRow('Comments', th.comments ? th.comments : 'Not provided')
    .addFileListRow(
      'Uploaded evidence',
      fileUtils.toDownloadableFiles(th.evidenceFiles, 'file-evidences-download/document'),
    )
    .create();
}
