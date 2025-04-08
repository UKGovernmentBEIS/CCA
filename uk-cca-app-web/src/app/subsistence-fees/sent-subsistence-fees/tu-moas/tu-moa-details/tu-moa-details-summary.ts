import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { SubsistenceFeesRunPaymentStatusPipe } from '@shared/pipes';
import { transformFileInfoToDownloadableFile } from '@shared/utils';

import { SubsistenceFeesMoaDetailsDTO } from 'cca-api';

export function toTuMoaDetailsSummary(details: SubsistenceFeesMoaDetailsDTO): SummaryData {
  const statusPipe = new SubsistenceFeesRunPaymentStatusPipe();
  const datePipe = new DatePipe('en-GB');
  const decimalPipe = new DecimalPipe('en-GB');

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Target unit ID', details?.businessId)
    .addRow('Operator', details?.name)
    .addFileListRow(
      'Target unit MoA file',
      transformFileInfoToDownloadableFile(details?.moaDocument, '../file-download'),
    )
    .addRow('Payment request date', datePipe.transform(details?.submissionDate, 'dd MMM yyyy'))
    .addRow('Payment status', statusPipe.transform(details?.paymentStatus))
    .addRow('Amount per facility (GBP)', decimalPipe.transform(details?.initialTotalAmount))
    .addRow('Facilities marked as paid', String(details?.paidFacilities))
    .addRow('Initial total amount (GBP)', decimalPipe.transform(details?.initialTotalAmount))
    .addRow('Current total amount (GBP)', decimalPipe.transform(details?.currentTotalAmount))
    .addRow('Received amount (GBP)', decimalPipe.transform(details?.receivedAmount))
    .create();
}
