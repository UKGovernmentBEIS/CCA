import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { Improvement } from '@shared/types';

import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

import { calculateOtherYearsVariableEnergy, MeasurementTypeToUnitEnum } from '../../underlying-agreement';
import { boolToString } from '../../utils';
import { toNumber } from '../utils';

export function toTPRBaselineDataDetails(
  referenceData: PerformanceDataFacilityReferenceData,
  productsLink?: string,
): SummaryData {
  const datePipe = new GovukDatePipe();
  const decimalPipe = new DecimalPipe('en-GB');
  const baselineAndTargets = referenceData?.baselineAndTargets;

  const productCount = baselineAndTargets?.variableEnergyConsumptionDataByProduct?.length;
  const productLabel =
    productCount === 0 ? 'No products added' : `${productCount} ${productCount === 1 ? 'Product' : 'Products'}`;

  const includedBaselineEnergyProducts = baselineAndTargets?.variableEnergyConsumptionDataByProduct?.filter(
    (product) => (product.productStatus ?? '').toUpperCase() !== 'EXCLUDED',
  );

  const otherYearsVariableEnergy = calculateOtherYearsVariableEnergy(
    includedBaselineEnergyProducts,
    new Date(baselineAndTargets?.baselineDate).getFullYear(),
    baselineAndTargets?.variableEnergyType,
  );

  return new SummaryFactory()
    .addSection('Details of baseline data')
    .addRow('Is at least 12 months of consecutive baseline data available?', 'Not provided')
    .addRow('Start date of baseline', datePipe.transform(baselineAndTargets?.baselineDate))
    .addRow(
      'Must the Special Reporting Methodology (SRM) be applied for this facility?',
      boolToString(baselineAndTargets?.usedReportingMechanism),
    )
    .addRow('Baseline energy to carbon factor (kgCe/kWh)', 'Not provided')

    .addSection('Details of baseline energy or carbon')
    .addRow(
      `Fixed baseline energy for the facility (${MeasurementTypeToUnitEnum[baselineAndTargets?.measurementType]})`,
      `${decimalPipe.transform(baselineAndTargets?.totalFixedEnergy, '1.0-7')}`,
    )
    .addRow('Is there a variable energy amount?', boolToString(!!baselineAndTargets?.baselineVariableEnergy))
    .addRow(
      'Indicate how you want to account for the portion of variable energy used (or carbon dioxide emitted) for your facility',
      baselineAndTargets?.variableEnergyType === 'BY_PRODUCT' ? 'Split by product' : 'Totals only',
    )
    .addRow('Products submitted', productLabel, { link: productCount ? `${productsLink ?? '../products'}` : '' })
    .addRow(
      `Variable baseline energy for the facility (${MeasurementTypeToUnitEnum[baselineAndTargets?.measurementType]})`,
      decimalPipe.transform(baselineAndTargets?.baselineVariableEnergy, '1.0-7'),
    )
    .addRow(
      `Total baseline energy for the facility (${MeasurementTypeToUnitEnum[baselineAndTargets?.measurementType]})`,
      decimalPipe.transform(
        toNumber(baselineAndTargets?.totalFixedEnergy) + toNumber(baselineAndTargets?.baselineVariableEnergy),
        '1.0-7',
      ),
    )
    .addRow(
      `Other years - variable baseline energy (${MeasurementTypeToUnitEnum[baselineAndTargets?.measurementType]})`,
      decimalPipe.transform(otherYearsVariableEnergy, '1.0-7'),
    )

    .addSection('Targets')
    .addRow(
      'TP7 (2026) improvement (%)',
      decimalPipe.transform(baselineAndTargets?.improvements[Improvement.TP7], '1.0-7'),
    )
    .addRow(
      'TP8 (2027 to 2028) improvement (%)',
      decimalPipe.transform(baselineAndTargets?.improvements[Improvement.TP8], '1.0-7'),
    )
    .addRow(
      'TP9 (2029 to 2030) improvement (%)',
      decimalPipe.transform(baselineAndTargets?.improvements[Improvement.TP9], '1.0-7'),
    )
    .create();
}

export function toTotalsOnlySummaryData(
  referenceData: PerformanceDataFacilityReferenceData,
  performanceData: PerformanceDataFacilityInputData,
  targetVariableEnergy: number | null,
  isEditable: boolean,
): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');

  const baselineAndTargets = referenceData?.baselineAndTargets;

  const factory = new SummaryFactory()
    .addSection('Target period throughput details', '../details')
    .addRow(
      `Total throughput (${baselineAndTargets?.throughputUnit})`,
      performanceData?.throughputDetails?.actualThroughput,
      {
        change: isEditable,
      },
    );

  if (targetVariableEnergy !== null) {
    factory
      .addSection('Calculated energy amounts', '../details')
      .addRow(
        `Total target variable energy (${MeasurementTypeToUnitEnum[baselineAndTargets?.measurementType]})`,
        decimalPipe.transform(targetVariableEnergy, '1.0-7'),
        {
          change: isEditable,
        },
      );
  }

  return factory.create();
}
