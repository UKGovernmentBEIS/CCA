import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { MarkingOfFacilitiesStatusPipe } from '@shared/pipes';

import { SubsistenceFeesMoaTargetUnitDetailsDTO } from 'cca-api';

export function toSectorMoaTUDetailsSummary(details: SubsistenceFeesMoaTargetUnitDetailsDTO): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const decimalPipe = new DecimalPipe('en-GB');
  const markingOfFacilitiesStatusPipe = new MarkingOfFacilitiesStatusPipe();

  const showMarkAll = details?.paidFacilities !== details?.totalFacilities && details?.totalFacilities !== 0;

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Target unit ID', String(details?.businessId))
    .addRow('Operator', details?.name)
    .addRow('Payment request date', datePipe.transform(details?.submissionDate, 'dd MMM yyyy'))
    .addRow(
      'Marking of facilities',
      markingOfFacilitiesStatusPipe.transform(details?.totalFacilities, details?.paidFacilities),
    )
    .addRow(
      'Facilities marked as paid',
      `${String(details?.paidFacilities)} out of ${String(details?.totalFacilities)}`,
      showMarkAll ? { action: 'Mark all as paid', actionLink: './mark-facilities/all-paid' } : {},
    )
    .addRow('Amount per facility (GBP)', decimalPipe.transform(details?.facilityFee))
    .addRow('Initial total amount (GBP)', decimalPipe.transform(details?.initialTotalAmount))
    .addRow('Current total amount (GBP)', decimalPipe.transform(details?.currentTotalAmount))
    .create();
}
