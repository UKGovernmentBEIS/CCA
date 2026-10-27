import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { Country } from '@shared/types';

import { FacilityInfoDTO } from 'cca-api';

export function toFacilityDetailsSummaryData(
  info: FacilityInfoDTO,
  roleType: string,
  countries: Country[],
): SummaryData {
  const datePipe = new DatePipe('en-GB');

  return new SummaryFactory()
    .addSection('Facility details', null, { testid: 'facility-details' })
    .addRow('Site name', info.siteName)
    .addTextAreaRow('Address', transformAddress(info.address, countries))

    .addSection('Subsistence fees', 'edit', { testid: 'subsistence-fees' })
    .addRow('Subsistence fees start date', datePipe.transform(info.chargeStartDate, 'dd/MM/yyyy') ?? 'Not provided')
    .addRow('Scheme exit date', datePipe.transform(info.schemeExitDate, 'dd/MM/yyyy') ?? 'Not provided', {
      change: roleType === 'REGULATOR',
    })
    .create();
}
