import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';
import { fileUtils, formatGbp } from '@shared/utils';

import { BuyOutSurplusTransactionDetailHistoryDTOPayload } from 'cca-api';

export function toCca3ChangeHistorySummaryData(
  payload: BuyOutSurplusTransactionDetailHistoryDTOPayload,
  transactionDetailId: number,
): SummaryData {
  if (!payload) return [];

  const summary = new SummaryFactory().addSection('');
  const datePipe = new GovukDatePipe();

  if ('amount' in payload) {
    summary.addRow('Current amount (GBP)', formatGbp(payload.amount));
  } else {
    summary.addRow('Status', new StatusPipe().transform(payload.paymentStatus));

    if (payload.paymentStatus === 'TERMINATED') return summary.create();

    if (payload.paymentStatus === 'PAID') {
      summary.addRow(
        'Date when the payment was received in the bank account',
        payload.paymentDate ? datePipe.transform(payload.paymentDate, 'date') : 'Not provided',
      );
    }

    if (payload.paymentStatus === 'UNDER_APPEAL') {
      summary.addRow(
        'Date on which the appeal was lodged',
        payload.dateOfAppeal ? datePipe.transform(payload.dateOfAppeal, 'date') : 'Not provided',
      );
    }
  }

  summary.addTextAreaRow('Comments', payload.comments || 'Not provided');

  const files = fileUtils.toDownloadableFiles(
    payload.evidenceFiles,
    `../file-evidences-download/${transactionDetailId}/attachment`,
  );

  if (files.length) {
    summary.addFileListRow('Uploaded evidence', files);
  } else {
    summary.addRow('Uploaded evidence', 'Not provided');
  }

  return summary.create();
}
