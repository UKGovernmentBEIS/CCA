import { CurrencyPipe, DecimalPipe, PercentPipe, TitleCasePipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { MeasurementTypeToUnitPipe } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { PerformanceOutcomePipe } from '@shared/pipes';
import { downloadFileInfoDTOFromAttachmentsUrl } from '@shared/utils';

import { AccountPerformanceReportDetailsDTO } from 'cca-api';

export function toReportSubmittedSummaryData(details: AccountPerformanceReportDetailsDTO) {
  const titleCasePipe = new TitleCasePipe();
  const govUkDatePipe = new GovukDatePipe();
  const decimalPipe = new DecimalPipe('en-GB');
  const currencyPipe = new CurrencyPipe('en-GB');
  const performanceOutcomePipe = new PerformanceOutcomePipe();
  const measurementTypeToUnitPipe = new MeasurementTypeToUnitPipe();
  const factory = new SummaryFactory();
  const percentPipe = new PercentPipe('en-GB');

  factory
    .addSection('Details')
    .addFileListRow(
      'Uploaded files',
      downloadFileInfoDTOFromAttachmentsUrl(details?.targetPeriodReport, '../file-download'),
    )
    .addRow('Submission date', govUkDatePipe.transform(details?.submissionDate))
    .addRow('Submission type', titleCasePipe.transform(details?.submissionType))
    .addRow('Uploaded version number', details?.reportVersion.toString())
    .addSection('TP Result')
    .addRow(
      `Target period performance (${
        details.targetType === 'RELATIVE'
          ? measurementTypeToUnitPipe.transform(details?.energyCarbonUnit) + '/' + details?.throughputUnit
          : measurementTypeToUnitPipe.transform(details?.energyCarbonUnit)
      })`,
      decimalPipe.transform(details?.tpPerformance, '1.3'),
    )
    .addRow('Target period improvement target %', percentPipe.transform(details?.percentTarget, '1.3'))
    .addRow('Target period improvement to base year %', percentPipe.transform(details?.tpPerformancePercent, '1.3'))
    .addRow('Target period result', performanceOutcomePipe.transform(details?.tpOutcome));

  if (details.submissionType === 'PRIMARY') {
    factory
      .addSection('Carbon surplus or buy-out determination')
      .addRow(
        'Carbon dioxide emitted (tCO2e)',
        decimalPipe.transform(details?.carbonSurplusBuyOutDTO?.co2Emissions, '1.3'),
      )
      .addRow('Surplus CO2e used', decimalPipe.transform(details?.carbonSurplusBuyOutDTO?.surplusUsed, '1.0'))
      .addRow('Surplus CO2e gained', decimalPipe.transform(details?.carbonSurplusBuyOutDTO?.surplusGained, '1.0'))
      .addRow(
        'Buy-out required (tCO2e)',
        decimalPipe.transform(details?.carbonSurplusBuyOutDTO?.priBuyOutCarbon, '1.0'),
      )
      .addRow('Buy-out cost (£)', currencyPipe.transform(details?.carbonSurplusBuyOutDTO?.priBuyOutCost, 'GBP'));
  }

  if (details.submissionType === 'SECONDARY') {
    factory
      .addSection('Supplementary MOA surplus and Buy-out determination')
      .addRow(
        'Carbon dioxide emitted (tCO2e)',
        decimalPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.co2Emissions, '1.3'),
      )
      .addRow(
        'Total target period Buy-out required (tCO2e)',
        decimalPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.priBuyOutCarbon, '1.3'),
      )
      .addRow(
        'Previous Buy-out required after use of surplus (tCO2e)',
        decimalPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.prevBuyOutCo2, '1.0'),
      )
      .addRow(
        'Previous surplus used (tCO2e)',
        decimalPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.prevSurplusUsed, '1.0'),
      )
      .addRow(
        'Previous surplus gained (tCO2e)',
        decimalPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.prevSurplusGained, '1.0'),
      )
      .addRow(
        'Secondary Buy-out required (tCO2e)',
        decimalPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.secondaryBuyOutCo2, '1.0'),
      )
      .addRow(
        'Secondary buy-out required cost or refund (£)',
        currencyPipe.transform(details?.secondaryMoASurplusBuyOutDTO?.secondaryBuyOutCost, 'GBP'),
      );
  }

  return factory.create();
}
