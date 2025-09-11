import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { NoticeRecipientsTypePipe, PerformanceOutcomePipe, TprVersionPipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

const decimalPipe = new DecimalPipe('en-GB');

export function toSurplusCalculatedSummaryData(
  payload: TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload,
): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const performanceOutcomePipe = new PerformanceOutcomePipe();
  const tprVersionPipe = new TprVersionPipe();

  const details = payload?.details;
  const surplusCalculatedDetails = payload?.surplusCalculatedDetails;
  const defaultContacts = payload?.defaultContacts;

  const summary = new SummaryFactory();

  // Details section
  summary
    .addSection('Details')
    .addRow(
      'TPR Version',
      `${tprVersionPipe.transform(details?.targetPeriodType, details?.performanceDataReportVersion)} (${performanceOutcomePipe.transform(details.tpOutcome)})`,
    )
    .addRow('Type', details.submissionType === 'PRIMARY' ? 'Primary' : 'Secondary');

  if (details.transactionCode) {
    summary.addRow('Transaction ID', details.transactionCode);
  }

  if (details.officialNotice) {
    summary.addFileListRow(
      'Refund letter',
      fileUtils.toDownloadableDocument([details.officialNotice], './file-download'),
    );
  }

  if (details.dueDate) {
    summary.addRow('Due date', datePipe.transform(details.dueDate, 'dd MMM yyyy'));
  }

  summary.addRow('Run ID', details.runId);

  // Surplus section
  summary
    .addSection('Surplus')
    .addRow('Surplus gained (tCO2e)', decimalPipe.transform(surplusCalculatedDetails.surplusGained));

  if (details.submissionType === 'SECONDARY' && details?.paymentStatus === 'AWAITING_REFUND') {
    summary.addRow(
      'Previous paid buy-out amount (GBP)',
      decimalPipe.transform(surplusCalculatedDetails.previousPaidFees),
    );

    summary.addRow('Overpayment buy-out amount (GBP)', decimalPipe.transform(surplusCalculatedDetails.overPaymentFee));
  }

  // Recipients section
  if (defaultContacts?.length) {
    const noticeRecipientsPipe = new NoticeRecipientsTypePipe();
    const recipients = defaultContacts?.map(
      (recipient) =>
        `${recipient.name}, ${noticeRecipientsPipe.transform(recipient.recipientType)}, ${recipient.email}`,
    );

    summary.addSection('Recipients').addRow('Users', recipients);
  }

  return summary.create();
}
