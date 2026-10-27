import { DatePipe, TitleCasePipe } from '@angular/common';

import { boolToString } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';

import { FacilityPerformanceAccountTemplateDataReportInfoDTO, FacilityPerformanceDataStatusInfoDTO } from 'cca-api';

export function toFacilityTPReportsSummaryData(
  dto: FacilityPerformanceDataStatusInfoDTO,
  type: 'INTERIM' | 'FINAL',
): SummaryData {
  const factory = new SummaryFactory();
  const titleCasePipe = new TitleCasePipe();

  // CCA-3293 omits these labels; retain them when older responses include them.
  const reportType =
    'reportType' in dto && typeof dto.reportType === 'string' ? titleCasePipe.transform(dto.reportType) : '';
  const submissionType =
    'submissionType' in dto && typeof dto.submissionType === 'string' && dto.submissionType
      ? ` (${titleCasePipe.transform(dto.submissionType)})`
      : '';
  const reportLabel = reportType + submissionType;
  const reportVersion = dto.reportVersion ? `${reportLabel ? ' - ' : ''}v${dto.reportVersion.toString()}` : '';

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
    .addRow('Last uploaded version', reportLabel + reportVersion)
    .addRow('Date of report submission', new DatePipe('en-GB').transform(dto.submissionDate, 'dd/MM/yyyy'));

  return factory.create();
}

export function toFacilityPATReportsSummaryData(dto: FacilityPerformanceAccountTemplateDataReportInfoDTO): SummaryData {
  return new SummaryFactory()
    .addSection('', '')
    .addRow('Reporting year', dto?.targetPeriodYear.toString())
    .addRow('Last uploaded version', dto?.reportVersion.toString())
    .addRow('Date of report submission', new DatePipe('en-GB').transform(dto?.submissionDate, 'dd/MM/yyyy'))
    .create();
}
