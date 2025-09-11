import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { SubsistenceFeesRunCompletedRequestActionPayload } from 'cca-api';

export function toSubsistenceFeesRunCompletedSummaryData(
  payload: SubsistenceFeesRunCompletedRequestActionPayload,
): SummaryData {
  const statusPipe = new StatusPipe();

  const summary = new SummaryFactory()
    .addSection('Details')
    .addRow('Payment request ID', payload?.paymentRequestId)
    .addRow('Charging year', String(payload?.chargingYear))
    .addRow('Status', statusPipe.transform(payload?.status))
    .addRow('Invoices sent', String(payload?.sentInvoices ?? 0));

  if (payload?.status === 'COMPLETED_WITH_FAILURES') {
    summary.addRow('Failed to send invoices', String(payload?.failedInvoices ?? 0));
  }

  if (payload?.report) {
    summary.addFileListRow('Detailed report', fileUtils.toDownloadableDocument([payload?.report], './file-download'));
  } else {
    summary.addRow('Detailed report', 'Nothing to report');
  }

  return summary.create();
}
