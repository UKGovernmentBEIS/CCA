import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { NoticeRecipientsTypePipe, PerformanceOutcomePipe, TprVersionPipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import {
  BuyOutCalculatedDetails,
  BuyOutSurplusDetails,
  TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload,
} from 'cca-api';

const decimalPipe = new DecimalPipe('en-GB');

export function toBuyoutFeeCalculatedSummaryData(
  payload: TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload,
): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const performanceOutcomePipe = new PerformanceOutcomePipe();
  const tprVersionPipe = new TprVersionPipe();

  const details = payload?.details;
  const buyOutCalculatedDetails = payload?.buyOutCalculatedDetails;
  const defaultContacts = payload?.defaultContacts;

  let summary = new SummaryFactory();

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
      details.paymentStatus === 'AWAITING_PAYMENT' ? 'Buy-out MoA file' : 'Refund letter',
      fileUtils.toDownloadableDocument([details.officialNotice], './file-download'),
    );
  }

  if (details.dueDate) {
    summary.addRow('Due date', datePipe.transform(details.dueDate, 'dd MMM yyyy'));
  }

  summary.addRow('Run ID', details.runId);

  // Buy-out section
  summary
    .addSection('Buy-out')
    .addRow('Amount (tCO2e)', decimalPipe.transform(buyOutCalculatedDetails.priBuyOutCarbon, '1.0'));

  summary =
    details.submissionType === 'PRIMARY'
      ? addPrimaryBuyout(summary, buyOutCalculatedDetails.buyOutFee)
      : addSecondaryBuyout(details?.paymentStatus, summary, buyOutCalculatedDetails);

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

function addPrimaryBuyout(summary: SummaryFactory, buyOutFee: string): SummaryFactory {
  return summary.addRow('Total buy-out fee (GBP)', decimalPipe.transform(buyOutFee));
}

function addSecondaryBuyout(
  paymentStatus: BuyOutSurplusDetails['paymentStatus'],
  summary: SummaryFactory,
  buyOutCalculatedDetails: BuyOutCalculatedDetails,
): SummaryFactory {
  summary.addRow('Total TPR buy-out amount (GBP)', decimalPipe.transform(buyOutCalculatedDetails.priBuyOutCost));
  summary.addRow('Previous paid buy-out amount (GBP)', decimalPipe.transform(buyOutCalculatedDetails.previousPaidFees));

  if (paymentStatus !== 'AWAITING_REFUND') {
    summary.addRow('Secondary buy-out amount (GBP)', decimalPipe.transform(buyOutCalculatedDetails.buyOutFee));
  } else {
    summary.addRow('Overpayment buy-out amount (GBP)', decimalPipe.transform(buyOutCalculatedDetails.buyOutFee));
  }

  return summary;
}
