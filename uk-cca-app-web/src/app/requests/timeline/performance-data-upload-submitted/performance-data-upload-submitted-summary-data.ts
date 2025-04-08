import { CurrencyPipe, DecimalPipe, PercentPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { MeasurementTypeToUnitPipe, TargetPeriodOutcomePipe } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { downloadFileInfoDTOFromAttachmentsUrl } from '@shared/utils';

import { PerformanceDataUploadedActionPayload } from './performance-data-upload-submitted.types';

export function toPerformanceUploadSubmittedSummaryData(
  payload: PerformanceDataUploadedActionPayload,
  creationDate?: string,
) {
  const tpOutcomePipe = new TargetPeriodOutcomePipe();
  const summaryFactory = new SummaryFactory();
  const govUkDatePipe = new GovukDatePipe();
  const decimalPipe = new DecimalPipe('en-GB');
  const currencyPipe = new CurrencyPipe('en-GB');
  const measurementTypeToUnitPipe = new MeasurementTypeToUnitPipe();
  const percentPipe = new PercentPipe('en-GB');

  summaryFactory
    .addSection('Details')
    .addFileListRow(
      'Uploaded Files',
      downloadFileInfoDTOFromAttachmentsUrl(payload.accountReportFile, './file-download'),
    )
    .addRow('Submission date', govUkDatePipe.transform(creationDate, 'datetime'))
    .addRow('Submission type', payload?.performanceData?.submissionType === 'PRIMARY' ? 'Primary' : 'Secondary')
    .addRow('Uploaded version number', payload?.performanceData?.reportVersion.toString())
    .addSection('TP Result')
    .addRow(
      `Target period performance (${
        payload.performanceData.targetType === 'RELATIVE'
          ? measurementTypeToUnitPipe.transform(payload?.performanceData?.targetUnitDetails?.energyCarbonUnit) +
            '/' +
            payload?.performanceData?.targetUnitDetails?.throughputUnit
          : measurementTypeToUnitPipe.transform(payload?.performanceData?.targetUnitDetails?.energyCarbonUnit)
      })`,
      decimalPipe.transform(payload?.performanceData?.performanceResult.tpPerformance, '1.3'),
    )
    .addRow(
      'Target period improvement %',
      percentPipe.transform(payload?.performanceData?.targetUnitDetails.percentTarget, '1.3'),
    )
    .addRow(
      'Target period improvement to base year %',
      percentPipe.transform(payload?.performanceData?.performanceResult.tpPerformancePercent, '1.3'),
    )
    .addRow('Target period result', tpOutcomePipe.transform(payload?.performanceData?.performanceResult.tpOutcome));

  if (payload.performanceData.submissionType === 'PRIMARY') {
    summaryFactory
      .addSection('Carbon surplus or buy-out determination')
      .addRow(
        'Carbon dioxide emitted (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.primaryDetermination.co2Emissions, '1.3'),
      )
      .addRow(
        'Surplus CO2e used',
        decimalPipe.transform(payload?.performanceData?.primaryDetermination.surplusUsed, '1.0'),
      )
      .addRow(
        'Surplus CO2e gained',
        decimalPipe.transform(payload?.performanceData?.primaryDetermination.surplusGained, '1.0'),
      )
      .addRow(
        'Buy-out required (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.primaryDetermination.priBuyOutCarbon, '1.0'),
      )
      .addRow(
        'Buy-out cost (£)',
        currencyPipe.transform(payload?.performanceData?.primaryDetermination.priBuyOutCost, 'GBP'),
      );
  }

  if (payload.performanceData.submissionType === 'SECONDARY') {
    summaryFactory
      .addSection('Supplementary MOA surplus and Buy-out determination')
      .addRow(
        'Carbon dioxide emitted (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.secondaryDetermination?.co2Emissions, '1.3'),
      )
      .addRow(
        'Total target period Buy-out required (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.secondaryDetermination.priBuyOutCarbon, '1.3'),
      )
      .addRow(
        'Previous Buy-out required after use of surplus (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.secondaryDetermination?.prevBuyOutCo2, '1.0'),
      )
      .addRow(
        'Previous surplus used (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.secondaryDetermination?.prevSurplusUsed, '1.0'),
      )
      .addRow(
        'Previous surplus gained (tCO2e)',
        decimalPipe.transform(payload.performanceData?.secondaryDetermination?.prevSurplusGained, '1.0'),
      )
      .addRow(
        'Secondary Buy-out required (tCO2e)',
        decimalPipe.transform(payload?.performanceData?.secondaryDetermination.secondaryBuyOutCo2, '1.0'),
      )
      .addRow(
        'Secondary buy-out required cost or refund (£)',
        currencyPipe.transform(payload?.performanceData?.secondaryDetermination.secondaryBuyOutCost, 'GBP'),
      );
  }

  return summaryFactory.create();
}
