import { DatePipe } from '@angular/common';

import { boolToString } from '@requests/common';
import { SummaryFactory } from '@shared/components';

import { FacilityPerformanceDataStatusInfoDTO } from 'cca-api';

export function toFacilityReportsSummaryData(dto: FacilityPerformanceDataStatusInfoDTO, type: 'INTERIM' | 'FINAL') {
  const factory = new SummaryFactory();

  factory.addSection('', '').addRow('Reporting period', dto.targetPeriodName);

  if (type === 'INTERIM') factory.addRow('Reporting year', dto.targetPeriodYear.toString());

  factory
    .addRow('Variation completed after submission', boolToString(dto.variationIndicator), {
      change: dto.variationIndicatorEditable,
      appendChangeParam: false,
      changeLink: `reports/${dto.targetPeriodYear}/variation-submission`,
    })
    .addRow('Locked', boolToString(dto.locked), {
      change: dto.lockEditable,
      appendChangeParam: false,
      changeLink: `reports/${dto.targetPeriodYear}/toggle-lock`,
    })
    .addRow('Last uploaded version', dto.reportVersion.toString())
    .addRow('Date of report submission', new DatePipe('en-GB').transform(dto.submissionDate, 'dd/MM/yyyy'));

  return factory.create();
}
