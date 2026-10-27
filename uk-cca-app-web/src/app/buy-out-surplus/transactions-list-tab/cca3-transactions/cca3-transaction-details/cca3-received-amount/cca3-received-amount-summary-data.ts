import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';
import BigNumber from 'bignumber.js';

import { BuyOutSurplusTransactionReceivedAmountHistoryDTO, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { Cca3ReceivedAmountForm } from './cca3-received-amount-form.provider';

export function signedAdjustment(draft: ReturnType<Cca3ReceivedAmountForm['getRawValue']>): BigNumber {
  return new BigNumber(draft.adjustmentAmount || '0').times(draft.changeType === 'subtract' ? -1 : 1);
}

export function toReceivedAmountSummaryData(transaction: FacilityBuyOutSurplusTransactionDTO): SummaryData {
  return new SummaryFactory()
    .addSection('')
    .addRow('Transaction ID', transaction.transactionCode)
    .addRow('Target unit ID', transaction.accountBusinessId)
    .addRow('Operator', transaction.operatorName)
    .addRow('Current expected amount (GBP)', new BigNumber(transaction.expectedAmount ?? '0').toFormat(2))
    .addRow('Current received amount (GBP)', new BigNumber(transaction.receivedAmount ?? '0').toFormat(2))
    .create();
}

export function toReceivedAmountReviewSummaryData(
  transaction: FacilityBuyOutSurplusTransactionDTO,
  draft: ReturnType<Cca3ReceivedAmountForm['getRawValue']>,
): SummaryData {
  const adjustment = signedAdjustment(draft);

  return new SummaryFactory()
    .addSection('', '..')
    .addRow('Change type', draft.changeType === 'add' ? 'Add' : 'Subtract', {
      change: true,
      appendChangeParam: false,
    })
    .addRow(`Amount ${draft.changeType === 'add' ? 'added' : 'subtracted'} (GBP)`, adjustment.abs().toFormat(2), {
      change: true,
      appendChangeParam: false,
    })
    .addRow('New received amount (GBP)', new BigNumber(transaction.receivedAmount ?? '0').plus(adjustment).toFormat(2))
    .addRow(
      'Date when the payment was received in the bank account',
      draft.paymentDate ? new GovukDatePipe().transform(draft.paymentDate) : 'Not provided',
      { change: true, appendChangeParam: false },
    )
    .addTextAreaRow('Comments', draft.comments, { change: true, appendChangeParam: false })
    .addFileListRow(
      'Uploaded evidence',
      fileUtils.toDownloadableFiles(
        fileUtils.toAttachments(draft.evidenceFiles),
        '../../file-evidences-download/attachment',
      ),
      { change: true, appendChangeParam: false },
    )
    .create();
}

export function toReceivedAmountHistorySummaryData(
  history: BuyOutSurplusTransactionReceivedAmountHistoryDTO,
): SummaryData {
  const amount = new BigNumber(history.adjustmentAmount);

  return new SummaryFactory()
    .addSection('Details')
    .addRow(`Amount ${amount.isNegative() ? 'subtracted' : 'added'} (GBP)`, amount.abs().toFormat(2))
    .addRow(
      'Date when the payment was received in the bank account',
      history.paymentDate ? new GovukDatePipe().transform(history.paymentDate) : 'Not provided',
    )
    .addTextAreaRow('Comments', history.comments)
    .addFileListRow(
      'Uploaded evidence',
      fileUtils.toDownloadableFiles(history.evidenceFiles, '../../../file-evidences-download/attachment'),
    )
    .create();
}

export function receivedAmountHistoryTitle(history: BuyOutSurplusTransactionReceivedAmountHistoryDTO): string {
  const amount = new BigNumber(history.adjustmentAmount);
  return `Amount ${amount.isNegative() ? 'subtracted' : 'added'} ${amount.abs().toFormat(2)} GBP by "${history.submitter}"`;
}
