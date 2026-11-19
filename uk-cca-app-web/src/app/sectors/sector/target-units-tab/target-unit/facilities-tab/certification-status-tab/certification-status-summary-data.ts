import { SummaryData, SummaryFactory } from '@shared/components';
import { DurationPipe, StatusPipe } from '@shared/pipes';

import { FacilityCertificationStatusDTO } from 'cca-api';

const SCHEME_HEADERS: Record<FacilityCertificationStatusDTO['schemeVersion'], string> = {
  CCA_3: 'CCA3 certification status',
  CCA_2: 'CCA2 certification status',
};

export function toCertificationStatusSummaryData(
  certifications: FacilityCertificationStatusDTO[],
  roleType: string,
): SummaryData {
  const statusPipe = new StatusPipe();
  const durationPipe = new DurationPipe();

  const factory = new SummaryFactory();

  for (const [schemeVersion, header] of Object.entries(SCHEME_HEADERS)) {
    const schemeCertifications = certifications.filter((c) => c.schemeVersion === schemeVersion);

    schemeCertifications.forEach((certificationDetails, index) => {
      // Only the first period of a scheme carries the header; the rest render as separate lists below it.
      factory
        .addSection(index === 0 ? header : null)
        .addRow('Certification period', certificationDetails.certificationPeriod)
        .addRow(
          'Certification duration',
          durationPipe.transform(
            certificationDetails.certificationPeriodStartDate,
            certificationDetails.certificationPeriodEndDate,
          ),
        )
        .addRow('Current status', statusPipe.transform(certificationDetails.status), {
          change: roleType === 'REGULATOR',
          changeLink: `${certificationDetails.certificationPeriod}/change-certification-status`,
          changeHiddenText: `${certificationDetails.certificationPeriod} current status`,
          appendChangeParam: false,
        });
    });
  }

  return factory.create();
}
