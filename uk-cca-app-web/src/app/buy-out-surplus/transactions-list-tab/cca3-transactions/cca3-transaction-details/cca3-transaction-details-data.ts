import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';
import { fileUtils, formatGbp } from '@shared/utils';
import BigNumber from 'bignumber.js';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { canMarkAllFacilities, markedFacilitiesCounts } from '../utils';

/**
 * Compares two decimal amounts for the reconciliation remarks. Returns null when either side is missing or both are
 * equal, so that no remark is displayed.
 */
function compareAmounts(amount?: string, reference?: string): 'higher' | 'lower' | null {
  if (!amount || !reference) return null;

  try {
    const amountValue = new BigNumber(amount);
    const referenceValue = new BigNumber(reference);
    if (!amountValue.isFinite() || !referenceValue.isFinite() || amountValue.eq(referenceValue)) return null;
    return amountValue.gt(referenceValue) ? 'higher' : 'lower';
  } catch {
    return null;
  }
}

export function toTransactionDetails(details: FacilityBuyOutSurplusTransactionDTO, isEditable: boolean): SummaryData {
  const statusPipe = new StatusPipe();
  const datePipe = new DatePipe('en-GB');

  const isRefund = details?.chargeType === 'REFUND';
  const showMarkAll = canMarkAllFacilities(details, isEditable);
  const counts = markedFacilitiesCounts(details);
  const markedFacilitiesValue = counts ? `${counts.marked} out of ${counts.expected}` : '';

  const summary = new SummaryFactory()
    .addSection('Details')
    .addRow('Target unit', details?.accountBusinessId)
    .addRow('Operator', details?.operatorName)
    .addFileListRow(
      isRefund ? 'Refund letter' : 'Buy-out MoA file',
      fileUtils.toDownloadableFileFromInfoDTO(
        Array.isArray(details?.fileInfoDTO) ? details?.fileInfoDTO : [details?.fileInfoDTO],
        'file-download',
      ),
    )
    .addRow(
      isRefund ? 'Total refund amount (GBP)' : 'Total buy-out amount (GBP)',
      formatGbp(details?.invoicedBuyOutFee),
    )
    .addRow('Date sent', datePipe.transform(details?.creationDate, 'dd MMM yyyy'));

  if (!isRefund) {
    summary.addRow('Due date', datePipe.transform(details?.dueDate, 'dd MMM yyyy'));
  }

  summary.addRow('Status', statusPipe.transform(details?.paymentStatus));

  if (isRefund) {
    return summary
      .addSection('Refund reconciliation')
      .addRow('Expected refund amount (GBP)', formatGbp(details?.expectedAmount))
      .addRow(
        'Facilities marked as refunded',
        markedFacilitiesValue,
        showMarkAll ? { action: 'Mark all as refunded', actionLink: './mark-facilities/all-refunded' } : {},
      )
      .addRow('Amount allocated to refunded facilities (GBP)', formatGbp(details?.amountAllocated))
      .create();
  }

  const receivedVsExpected = compareAmounts(details?.receivedAmount, details?.expectedAmount);
  const allocatedVsReceived = compareAmounts(details?.amountAllocated, details?.receivedAmount);

  const receivedAmountRemark = receivedVsExpected
    ? ` (This amount is ${receivedVsExpected} than the expected amount)`
    : '';
  const allocatedAmountRemark = allocatedVsReceived
    ? ` (This amount is ${allocatedVsReceived} than the received amount)`
    : '';

  return summary
    .addSection('Buy-out fee reconciliation')
    .addRow('Expected amount (GBP)', formatGbp(details?.expectedAmount))
    .addRow('Received amount (GBP)', `${formatGbp(details?.receivedAmount)}${receivedAmountRemark}`, {
      change: isEditable,
      changeLink: isEditable ? './received-amount' : null,
      appendChangeParam: false,
    })
    .addRow(
      'Facilities marked as paid',
      markedFacilitiesValue,
      showMarkAll ? { action: 'Mark all as paid', actionLink: './mark-facilities/all-paid' } : {},
    )
    .addRow(
      'Amount allocated to paid facilities (GBP)',
      `${formatGbp(details?.amountAllocated)}${allocatedAmountRemark}`,
    )
    .create();
}
