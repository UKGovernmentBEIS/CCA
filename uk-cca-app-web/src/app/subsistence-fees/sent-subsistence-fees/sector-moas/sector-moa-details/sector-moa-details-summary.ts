import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { SubsistenceFeesRunPaymentStatusPipe } from '@shared/pipes';
import { transformFileInfoToDownloadableFile } from '@shared/utils';

import { SubsistenceFeesMoaDetailsDTO } from 'cca-api';

export function toSectorMoaDetailsSummary(details: SubsistenceFeesMoaDetailsDTO): SummaryData {
  const statusPipe = new SubsistenceFeesRunPaymentStatusPipe();
  const datePipe = new DatePipe('en-GB');
  const decimalPipe = new DecimalPipe('en-GB');

  const higherReceivedAmount =
    details?.receivedAmount > details?.currentTotalAmount
      ? ' (This amount is higher than the current total amount)'
      : '';

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Sector', details?.name)
    .addFileListRow('Sector MoA file', transformFileInfoToDownloadableFile(details?.moaDocument, '../file-download'))
    .addRow('Payment request date', datePipe.transform(details?.submissionDate, 'dd MMM yyyy'))
    .addRow('Payment status', statusPipe.transform(details?.paymentStatus))
    .addRow(
      'Facilities marked as paid',
      `${String(details?.paidFacilities)} out of ${String(details?.totalFacilities)}`,
    )
    .addRow('Initial total amount (GBP)', decimalPipe.transform(details?.initialTotalAmount))
    .addRow('Current total amount (GBP)', decimalPipe.transform(details?.currentTotalAmount))
    .addRow('Received amount (GBP)', `${decimalPipe.transform(details?.receivedAmount)}${higherReceivedAmount}`)
    .create();
}
