import { PercentPipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';
import { TargetTypePipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { AccountPerformanceAccountTemplateDataReportDetailsDTO } from 'cca-api';

export function toPatReportSubmittedSummaryData(reportDetails: AccountPerformanceAccountTemplateDataReportDetailsDTO) {
  const payload = reportDetails.data;
  const percentPipe = new PercentPipe('en-GB');
  const targetTypePipe = new TargetTypePipe();

  return new SummaryFactory()
    .addSection('')
    .addFileListRow('Uploaded file', fileUtils.toDownloadableFileFromInfoDTO([payload?.file], '../file-download'))
    .addRow('Target type', targetTypePipe.transform(payload?.targetUnitIdentityAndPerformance?.targetType))
    .addRow('TP6 Target %', percentPipe.transform(payload?.targetUnitIdentityAndPerformance?.targetPercentage, '1.3'))
    .addRow(
      'TP6 Improvement Achieved %',
      percentPipe.transform(payload?.targetUnitIdentityAndPerformance?.improvementAchievedPercentage, '1.3'),
    )
    .addRow(
      'TP6 Improvement Accounted For %',
      percentPipe.transform(payload?.targetUnitIdentityAndPerformance?.improvementAccountedPercentage, '1.3'),
    )
    .addRow(
      "Was this period's performance impacted by any   implemented measures?",
      payload?.targetUnitIdentityAndPerformance?.performanceImpactedByAnyImplementedMeasures,
    )
    .addRow(
      'Supporting text for no impact ',
      payload?.targetUnitIdentityAndPerformance?.performanceImpactedByAnyImplementedMeasuresSupportingText,
    )
    .addSection('')
    .addRow(
      'Estimated change in energy consumption (%)',
      percentPipe.transform(
        payload?.targetUnitIdentityAndPerformance?.totalEstimateChangeInEnergyConsumptionPercentage,
        '1.3',
      ),
    )
    .addRow(
      'Estimated change in carbon emissions (%)',
      percentPipe.transform(
        payload?.targetUnitIdentityAndPerformance?.totalEstimateChangeInCarbonEmissionsPercentage,
        '1.3',
      ),
    )
    .create();
}
