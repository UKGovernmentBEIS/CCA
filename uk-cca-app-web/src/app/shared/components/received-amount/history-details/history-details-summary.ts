import { DecimalPipe } from '@angular/common';

import { fileUtils } from '@shared/utils';

import { SubsistenceFeesMoaReceivedAmountHistoryDTO } from 'cca-api';

import { SummaryFactory } from '../../summary/summary.factory';
import { SummaryData } from '../../summary/type';

export function toHistoryDetailsSummary(history: SubsistenceFeesMoaReceivedAmountHistoryDTO): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');

  const transactionAmount =
    +history?.transactionAmount < 0 ? history?.transactionAmount.split('-')[1] : history?.transactionAmount;

  return new SummaryFactory()
    .addSection('Details')
    .addRow(
      `Amount ${+history?.transactionAmount > 0 ? 'added' : 'subtracted'} (GBP)`,
      decimalPipe.transform(transactionAmount),
    )
    .addRow('Comments', history?.comments || '')
    .addFileListRow(
      'Uploaded evidence',
      fileUtils.toDownloadableFiles(history?.evidenceFiles, '../../evidence-file-download/attachment'),
    )
    .create();
}
