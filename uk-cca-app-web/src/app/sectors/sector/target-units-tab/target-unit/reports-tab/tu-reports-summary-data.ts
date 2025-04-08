import { PerformanceDataTargetPeriodEnum } from '@requests/common';
import { SummaryFactory } from '@shared/components';

import { AccountPerformanceDataStatusInfoDTO } from 'cca-api';

export function toTuReportsSummaryData(
  dto: AccountPerformanceDataStatusInfoDTO,
  targetPeriodType: PerformanceDataTargetPeriodEnum,
) {
  return new SummaryFactory()
    .addSection('', `${targetPeriodType}/toggle-lock`)
    .addRow('Current reporting period', dto.targetPeriodName)
    .addRow('Locked', dto.locked ? 'Yes' : 'No', { change: dto.editable, appendChangeParam: false })
    .addRow('Last uploaded version', dto.reportVersion.toString())
    .create();
}
