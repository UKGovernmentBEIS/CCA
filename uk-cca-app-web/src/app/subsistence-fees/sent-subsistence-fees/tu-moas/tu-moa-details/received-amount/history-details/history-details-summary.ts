import { DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { SubsistenceFeesMoaReceivedAmountHistoryDTO } from 'cca-api';

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
