import { PercentPipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';
import { TargetTypePipe } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { PATUploadedActionPayload } from './pat-upload-submitted.types';

export function toPatSubmittedSummaryData(payload: PATUploadedActionPayload) {
  const percentPipe = new PercentPipe('en-GB');
  const targetTypePipe = new TargetTypePipe();

  return new SummaryFactory()
    .addSection('')
    .addRow('TU Identifier', payload?.businessId)
    .addFileListRow('Uploaded file', fileUtils.toDownloadableFileFromInfoDTO([payload?.data?.file], './file-download'))
    .addRow('Target type', targetTypePipe.transform(payload?.data?.targetUnitIdentityAndPerformance?.targetType))
    .addRow(
      'TP6 Target %',
      percentPipe.transform(payload?.data?.targetUnitIdentityAndPerformance?.targetPercentage, '1.3'),
    )
    .addRow(
      'TP6 Improvement Achieved %',
      percentPipe.transform(payload?.data?.targetUnitIdentityAndPerformance?.improvementAchievedPercentage, '1.3'),
    )
    .addRow(
      'TP6 Improvement Accounted For %',
      percentPipe.transform(payload?.data?.targetUnitIdentityAndPerformance?.improvementAccountedPercentage, '1.3'),
    )
    .addRow(
      "Was this period's performance impacted by any   implemented measures?",
      payload?.data?.targetUnitIdentityAndPerformance?.performanceImpactedByAnyImplementedMeasures,
    )
    .addRow(
      'Supporting text for no impact ',
      payload?.data?.targetUnitIdentityAndPerformance?.performanceImpactedByAnyImplementedMeasuresSupportingText,
    )

    .addSection('')
    .addRow(
      'Estimated change in energy consumption (%)',
      percentPipe.transform(
        payload?.data?.targetUnitIdentityAndPerformance?.totalEstimateChangeInEnergyConsumptionPercentage,
        '1.3',
      ),
    )
    .addRow(
      'Estimated change in carbon emissions (%)',
      percentPipe.transform(
        payload?.data?.targetUnitIdentityAndPerformance?.totalEstimateChangeInCarbonEmissionsPercentage,
        '1.3',
      ),
    )
    .create();
}
