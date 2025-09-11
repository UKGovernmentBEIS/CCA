import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { SubsistenceFeesMoaDetailsDTO } from 'cca-api';

export function toSectorMoaDetailsSummary(details: SubsistenceFeesMoaDetailsDTO, isEditable: boolean): SummaryData {
  const statusPipe = new StatusPipe();
  const datePipe = new DatePipe('en-GB');
  const decimalPipe = new DecimalPipe('en-GB');

  const showMarkAll = details?.paidFacilities !== details?.totalFacilities && details?.totalFacilities !== 0;

  const higherReceivedAmount =
    Number(details?.receivedAmount) > Number(details?.currentTotalAmount)
      ? ' (This amount is higher than the current total amount)'
      : '';

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Sector', `${details?.businessId} - ${details?.name}`)
    .addFileListRow('Sector MoA file', fileUtils.toDownloadableDocument([details?.moaDocument], '../file-download'))
    .addRow('Payment request date', datePipe.transform(details?.submissionDate, 'dd MMM yyyy'))
    .addRow('Payment status', statusPipe.transform(details?.paymentStatus))
    .addRow(
      'Facilities marked as paid',
      `${String(details?.paidFacilities)} out of ${String(details?.totalFacilities)}`,
      showMarkAll ? { action: 'Mark all as paid', actionLink: './mark-facilities/all-paid' } : {},
    )
    .addRow('Initial total amount (GBP)', decimalPipe.transform(details?.initialTotalAmount))
    .addRow('Current total amount (GBP)', decimalPipe.transform(details?.currentTotalAmount))
    .addRow('Received amount (GBP)', `${decimalPipe.transform(details?.receivedAmount)}${higherReceivedAmount}`, {
      change: isEditable,
      changeLink: isEditable ? './received-amount' : null,
      appendChangeParam: false,
    })
    .create();
}
