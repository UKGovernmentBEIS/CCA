import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { SubsistenceFeesRunDetailsDTO } from 'cca-api';

export function toSentSubsistenceFeesDetails(details: SubsistenceFeesRunDetailsDTO): SummaryData {
  const statusPipe = new StatusPipe();
  const datePipe = new DatePipe('en-GB');
  const decimalPipe = new DecimalPipe('en-GB');

  return new SummaryFactory()
    .addSection('Subsistence fees run details')
    .addRow('Payment request date', datePipe.transform(details.submissionDate, 'dd MMM yyyy'))
    .addRow('Payment status', statusPipe.transform(details.paymentStatus))
    .addRow(
      'Total (GBP)',
      `${decimalPipe.transform(details.currentTotalAmount)} (initially ${decimalPipe.transform(details.initialTotalAmount)})`,
    )
    .addRow('Outstanding (GBP)', decimalPipe.transform(details.outstandingTotalAmount))
    .create();
}
