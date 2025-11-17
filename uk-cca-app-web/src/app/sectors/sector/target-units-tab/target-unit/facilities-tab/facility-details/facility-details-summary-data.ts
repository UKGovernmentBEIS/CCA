import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { DurationPipe, StatusPipe, transformAddress } from '@shared/pipes';

import { FacilityInfoDTO } from 'cca-api';

export function toFacilityDetailsSummaryData(info: FacilityInfoDTO, roleType: string): SummaryData {
  const statusPipe = new StatusPipe();
  const durationPipe = new DurationPipe();
  const datePipe = new DatePipe('en-GB');

  const factory = new SummaryFactory()
    .addSection('Facility details', null, { testid: 'facility-details' })
    .addRow('Site name', info.siteName)
    .addTextAreaRow('Address', transformAddress(info.address))

    .addSection('Subsistence fees', 'edit', { testid: 'subsistence-fees' })
    .addRow('Subsistence fees start date', datePipe.transform(info.chargeStartDate, 'dd/MM/yyyy') ?? 'Not provided')
    .addRow('Scheme exit date', datePipe.transform(info.schemeExitDate, 'dd/MM/yyyy') ?? 'Not provided', {
      change: roleType === 'REGULATOR',
    });

  for (const certificationDetails of info.facilityCertificationDetails) {
    factory
      .addSection('Certification period')
      .addRow('Certification period', certificationDetails.certificationPeriod)
      .addRow('Current status', statusPipe.transform(certificationDetails.status), {
        change: roleType === 'REGULATOR',
        changeLink: `${certificationDetails.certificationPeriod}/change-certification-status`,
      });

    if (certificationDetails.status === 'CERTIFIED') {
      factory.addRow(
        'Certification duration',
        durationPipe.transform(certificationDetails.startDate, certificationDetails.certificationPeriodEndDate),
      );
    }
  }

  return factory.create();
}
