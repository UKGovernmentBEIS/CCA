import { DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { MEASUREMENT_TYPE_TO_UNIT_MAP } from '@shared/pipes';
import { toNumber } from '@shared/utils';

import { PerformanceDataFacilityCalculatedResults, PerformanceDataFacilityReferenceData } from 'cca-api';

import { isCarbonMeasurementType, resolveMeasurementUnit } from '../../utils';

type ComparisonSummaryInput = {
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9';
  reportType: 'INTERIM' | 'FINAL';
  referenceData: PerformanceDataFacilityReferenceData;
  calculatedResults: PerformanceDataFacilityCalculatedResults;
};

type ResultsDetailsSummaryInput = {
  reportType: 'INTERIM' | 'FINAL';
  calculatedResults: PerformanceDataFacilityCalculatedResults;
};

function formatNumber(value: string | number | undefined, decimalPipe: DecimalPipe, digits = '1.0-7'): string {
  return decimalPipe.transform(toNumber(value), digits) ?? '0';
}

function formatPercentage(value: string | number | undefined, decimalPipe: DecimalPipe): string {
  const numericValue = toNumber(value);
  const normalizedPercent = numericValue > 1 ? numericValue : numericValue * 100;
  return `${decimalPipe.transform(normalizedPercent, '1.0-3') ?? '0'}%`;
}

export function toComparisonDataSummaryData({
  targetPeriodType,
  reportType,
  referenceData,
  calculatedResults,
}: ComparisonSummaryInput): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');

  const measurementUnit = resolveMeasurementUnit(referenceData);
  const isCarbonMeasurement = isCarbonMeasurementType(measurementUnit);

  const factory = new SummaryFactory()
    .addSection('', '')
    .addRow('Reporting period', targetPeriodType ?? '')
    .addSection('Comparison between actual and target amounts');

  if (!isCarbonMeasurement) {
    factory
      .addRow(`Actual energy (${measurementUnit})`, formatNumber(calculatedResults.actualEnergyCarbon, decimalPipe))
      .addRow(`Target energy (${measurementUnit})`, formatNumber(calculatedResults.targetEnergyCarbon, decimalPipe))
      .addRow(
        `Energy difference (${measurementUnit})`,
        formatNumber(calculatedResults.energyCarbonDifference, decimalPipe),
      );
  }

  return factory
    .addRow(
      `Target period weighted conversion factor (kgCO2e/${isCarbonMeasurement ? MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH : measurementUnit})`,
      formatNumber(calculatedResults.weightedConversionFactor, decimalPipe),
    )
    .addRow('Actual tCO2e emitted (tCO2e)', formatNumber(calculatedResults.actualCo2Emissions, decimalPipe, '1.0-7'))
    .addRow('Target tCO2e emitted (tCO2e)', formatNumber(calculatedResults.targetCo2Emissions, decimalPipe, '1.0-7'))
    .addRow('tCO2e difference (tCO2e)', formatNumber(calculatedResults.co2EmissionsDifference, decimalPipe, '1.0-7'))
    .addRow(
      reportType === 'INTERIM' ? 'Interim target %' : 'Improvement target %',
      formatPercentage(calculatedResults.targetImprovement, decimalPipe),
    )
    .addRow('Actual improvement % achieved', formatPercentage(calculatedResults.actualImprovement, decimalPipe))
    .create();
}

export function toResultsDetailsSummaryData({
  reportType,
  calculatedResults,
}: ResultsDetailsSummaryInput): SummaryData {
  if (reportType === 'INTERIM') return new SummaryFactory().create();

  const decimalPipe = new DecimalPipe('en-GB');

  const factory = new SummaryFactory()
    .addSection('', '')
    .addRow(
      'Target period result',
      calculatedResults?.targetPeriodResultType === 'TARGET_MET' ? 'Target met' : 'Target not met',
    );

  if (calculatedResults?.targetPeriodResultType === 'TARGET_MET') {
    const persistedSurplus = toNumber(calculatedResults?.surplusGained);
    factory.addRow('Total surplus gained (tCO2e)', formatNumber(persistedSurplus, decimalPipe, '1.0-0'));
  } else {
    const persistedBuyOut = toNumber(calculatedResults?.buyOutRequired);
    factory.addRow('Total buy-out required (tCO2e)', formatNumber(persistedBuyOut, decimalPipe, '1.0-0'));
  }

  return factory.create();
}
