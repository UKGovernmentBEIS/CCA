import { DecimalPipe, TitleCasePipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryFactory } from '@shared/components';
import { PerformanceOutcomePipe, StatusPipe, TprVersionPipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

export function toTransactionDetailsSummaryData(txd: BuyOutSurplusTransactionDetailsDTO) {
  const factory = new SummaryFactory();
  const performanceOutcomePipe = new PerformanceOutcomePipe();
  const tprVersionPipe = new TprVersionPipe();
  const titleCasePipe = new TitleCasePipe();
  const datePipe = new GovukDatePipe();
  const statusPipe = new StatusPipe();
  const decimalPipe = new DecimalPipe('en-GB');

  const isTransactionTerminated = txd?.paymentStatus !== 'TERMINATED';

  factory
    .addSection('Details', 'change-status')
    .addRow('Target Unit', txd?.accountBusinessId)
    .addRow('Operator', txd?.operatorName)
    .addRow(
      'TPR Version',
      `${tprVersionPipe.transform(txd?.targetPeriodType, Number(txd?.reportVersion))}  (${performanceOutcomePipe.transform(txd?.targetPeriodResultType)})`,
    )
    .addRow('Type', titleCasePipe.transform(txd?.submissionType))
    .addFileListRow(
      txd?.chargeType === 'REFUND' ? 'Refund letter' : 'Buy-out MoA file',
      fileUtils.toDownloadableFileFromInfoDTO([txd?.fileInfoDTO], 'file-download'),
    )
    .addRow('Date sent', datePipe.transform(txd?.creationDate, 'date'))
    .addRow('Due date', datePipe?.transform(txd?.dueDate, 'date'))
    .addRow('Status', statusPipe.transform(txd?.paymentStatus), {
      change: isTransactionTerminated,
      appendChangeParam: false,
    })
    .addSection('Buy-out', 'change-amount');

  if (txd?.submissionType === 'PRIMARY') {
    factory
      .addRow('Amount (tCO2e)', decimalPipe.transform(txd?.priBuyOutCarbon, '1.0'))
      .addRow('Total buy-out fee (GBP)', decimalPipe.transform(txd?.priBuyOutCost, '1.2'))
      .addRow('Current buy-out fee (GBP)', decimalPipe.transform(txd?.buyOutFee, '1.2'), {
        change: isTransactionTerminated,
        appendChangeParam: false,
      });
  }

  if (txd?.submissionType === 'SECONDARY' && txd?.chargeType === 'FEE') {
    factory
      .addRow('Amount (tCO2e)', decimalPipe.transform(txd?.priBuyOutCarbon, '1.0'))
      .addRow('Total buy-out fee (GBP)', decimalPipe.transform(txd?.priBuyOutCost, '1.2'))
      .addRow('Previous paid fees (GBP)', decimalPipe.transform(txd?.invoicedPreviousPaidFees, '1.2'))
      .addRow('Secondary buy-out fee (GBP)', decimalPipe.transform(txd?.invoicedBuyOutFee, '1.2'))
      .addRow('Current secondary buy-out fee (GBP)', decimalPipe.transform(txd?.buyOutFee, '1.2'), {
        change: isTransactionTerminated,
        appendChangeParam: false,
      });
  }

  if (
    txd?.submissionType === 'SECONDARY' &&
    txd?.chargeType === 'REFUND' &&
    txd?.targetPeriodResultType === 'TARGET_MET'
  ) {
    factory
      .addRow('Surplus gained (tCO2e)', decimalPipe.transform(txd?.invoicedSurplusGained, '1.0'))
      .addRow('Previous paid fees (GBP)', decimalPipe.transform(txd?.invoicedPreviousPaidFees, '1.2'))
      .addRow('Refund amount (GBP)', decimalPipe.transform(txd?.invoicedBuyOutFee, '1.2'))
      .addRow('Current refund amount (GBP)', decimalPipe.transform(txd?.buyOutFee, '1.2'), {
        change: isTransactionTerminated,
        appendChangeParam: false,
      });
  }

  if (
    txd?.submissionType === 'SECONDARY' &&
    txd?.chargeType === 'REFUND' &&
    txd?.targetPeriodResultType === 'BUY_OUT_REQUIRED'
  ) {
    factory
      .addRow('Amount (tCO2e)', decimalPipe.transform(txd?.priBuyOutCarbon, '1.0'))
      .addRow('Total buy-out fee (GBP)', decimalPipe.transform(txd?.priBuyOutCost, '1.2'))
      .addRow('Previous paid fees (GBP)', decimalPipe.transform(txd?.invoicedPreviousPaidFees, '1.2'))
      .addRow('Refund amount (GBP)', decimalPipe.transform(txd?.invoicedBuyOutFee, '1.2'))
      .addRow('Current refund amount (GBP)', decimalPipe.transform(txd?.buyOutFee, '1.2'), {
        change: isTransactionTerminated,
        appendChangeParam: false,
      });
  }

  return factory.create();
}
