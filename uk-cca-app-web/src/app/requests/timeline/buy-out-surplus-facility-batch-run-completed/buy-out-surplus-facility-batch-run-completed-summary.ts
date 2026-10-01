import { DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

// import { BuyOutSurplusFacilityRunCompletedRequestActionPayload } from 'cca-api';

export function toBuyOutSurplusFacilityBatchRunCompletedSummaryData(
  // BuyOutSurplusFacilityRunCompletedRequestActionPayload
  payload: any,
  actionType: string,
  runId: string,
): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');
  const runSummary = payload?.runSummary;

  const summary = new SummaryFactory().addSection('Details').addRow('Run ID', runId);

  summary.addRow(
    'Status',
    actionType === 'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES' ? 'Completed with failures' : 'Completed',
  );

  if (payload?.csvFile) {
    summary.addFileListRow(
      'Batch run summary report',
      fileUtils.toDownloadableFileFromInfoDTO([payload.csvFile], './file-download'),
    );
  }

  summary.addRow('Total target units', decimalPipe.transform(runSummary?.totalAccounts ?? 0));

  if (actionType === 'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES') {
    summary.addRow('Failed target units', decimalPipe.transform(runSummary?.failedAccounts ?? 0));
    summary.addRow('Failed facilities', decimalPipe.transform(runSummary?.failedFacilities ?? 0));
  }

  for (const entry of runSummary?.runSummaryEntries ?? []) {
    summary
      .addSection(entry.targetPeriod)
      .addRow('Cost (GBP / tCO2e)', decimalPipe.transform(entry.buyOutCost ?? 0))
      .addRow('Total facilities', decimalPipe.transform(entry.totalFacilities ?? 0))
      .addRow('Total buy-out transactions', decimalPipe.transform(entry.totalBuyOutTransactions ?? 0))
      .addRow('Total buy-out facilities', decimalPipe.transform(entry.totalBuyOutFacilities ?? 0))
      .addRow('Total refund transactions', decimalPipe.transform(entry.totalRefundTransactions ?? 0))
      .addRow('Total refund facilities', decimalPipe.transform(entry.totalRefundFacilities ?? 0));
  }

  return summary.create();
}
