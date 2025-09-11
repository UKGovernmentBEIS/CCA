import { DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';
import BigNumber from 'bignumber.js';

import { SubsistenceFeesMoaReceivedAmountDetailsDTO } from 'cca-api';

export function toReceivedAmountSummaryData(
  changeType: 'add' | 'subtract',
  businessId: string,
  transactionId: string,
  name: string,
  details: SubsistenceFeesMoaReceivedAmountDetailsDTO,
  receivedAmount: string,
  isEditable: boolean,
): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');

  const transactionAmount =
    changeType === 'add' ? details?.transactionAmount.split('+')[1] : details?.transactionAmount.split('-')[1];

  const bigReceivedAmount = new BigNumber(receivedAmount);
  const bigTransactionAmount = new BigNumber(transactionAmount);

  const newReceivedAmount =
    changeType === 'add' ? bigReceivedAmount.plus(bigTransactionAmount) : bigReceivedAmount.minus(bigTransactionAmount);

  return new SummaryFactory()
    .addSection('', '..')
    .addRow('Sector', `${businessId}-${name}`)
    .addRow('Transaction ID', transactionId)
    .addRow(
      `${changeType === 'add' ? 'Added' : 'Subtracted'} payment (GBP)`,
      decimalPipe.transform(new BigNumber(transactionAmount).toNumber()),
      { change: isEditable, appendChangeParam: false },
    )
    .addRow('New received payment (GBP)', decimalPipe.transform(newReceivedAmount.toNumber()))
    .addTextAreaRow('Comments', details?.comments || '', { change: isEditable, appendChangeParam: false })
    .addFileListRow(
      'Uploaded evidence',
      fileUtils.toDownloadableFiles(details?.evidenceFiles, '../evidence-file-download/attachment'),
      { change: isEditable, appendChangeParam: false },
    )
    .create();
}
