import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { Country } from '@shared/types';
import { formatCalendarDate } from '@shared/utils';

import { FacilityInfoDTO } from 'cca-api';

export function toFacilityDetailsSummaryData(
  info: FacilityInfoDTO,
  roleType: string,
  countries: Country[],
): SummaryData {
  return new SummaryFactory()
    .addSection('Facility details', null, { testid: 'facility-details' })
    .addRow('Site name', info.siteName)
    .addTextAreaRow('Address', transformAddress(info.address, countries))

    .addSection('Subsistence fees', 'edit', { testid: 'subsistence-fees' })
    .addRow('Subsistence fees start date', formatCalendarDate(info.chargeStartDate, 'dd/MM/yyyy') ?? 'Not provided')
    .addRow('Scheme exit date', formatCalendarDate(info.schemeExitDate, 'dd/MM/yyyy') ?? 'Not provided', {
      change: roleType === 'REGULATOR',
    })
    .create();
}
