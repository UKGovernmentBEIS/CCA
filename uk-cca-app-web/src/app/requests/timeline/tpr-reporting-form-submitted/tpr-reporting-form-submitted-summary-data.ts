import { TitleCasePipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';

import { PerformanceDataFacilitySubmissionDetails } from 'cca-api';

export function toTPRReportingSubmittedSummary(details: PerformanceDataFacilitySubmissionDetails): SummaryData {
  const titleCasePipe = new TitleCasePipe();
  const govUkDatePipe = new GovukDatePipe();

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Reporting period', details?.targetPeriodType)
    .addRow(
      'Report type',
      `${titleCasePipe.transform(details?.reportType)}${
        details?.submissionType ? ` (${titleCasePipe.transform(details.submissionType)})` : ''
      } - v${details?.reportVersion}`,
    )
    .addRow('Created', `${govUkDatePipe.transform(details?.creationDate)}`)
    .addRow('Submitted', `${govUkDatePipe.transform(details?.submissionDate, 'datetime')}`)
    .create();
}
