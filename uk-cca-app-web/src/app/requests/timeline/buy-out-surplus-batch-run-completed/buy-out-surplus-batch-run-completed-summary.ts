import { DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { BuyOutSurplusRunCompletedRequestActionPayload } from 'cca-api';

export function toBuyoutSurplusBatchRunCompletedSummaryData(
  payload: BuyOutSurplusRunCompletedRequestActionPayload,
  actionType: string,
): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');

  const summary = new SummaryFactory().addSection('Details');

  switch (actionType) {
    case 'BUY_OUT_SURPLUS_RUN_COMPLETED':
      summary.addRow('Status', 'Completed');
      summary.addRow('Total target units', decimalPipe.transform(payload?.runSummary.totalAccounts ?? 0));
      break;

    case 'BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES':
      summary.addRow('Status', 'Completed with failures');
      summary.addRow('Total target units', decimalPipe.transform(payload?.runSummary.totalAccounts ?? 0));
      summary.addRow('Failed target units', decimalPipe.transform(payload?.runSummary.failedAccounts ?? 0));
      break;
  }

  summary.addRow('Total buy-out transactions', decimalPipe.transform(payload?.runSummary.buyOutTransactions ?? 0));
  summary.addRow('Total refund transactions', decimalPipe.transform(payload?.runSummary.refundedTransactions ?? 0));

  if (payload?.csvFile) {
    summary.addFileListRow(
      'Batch run summary report',
      fileUtils.toDownloadableFileFromInfoDTO([payload.csvFile], './file-download'),
    );
  }

  return summary.create();
}
