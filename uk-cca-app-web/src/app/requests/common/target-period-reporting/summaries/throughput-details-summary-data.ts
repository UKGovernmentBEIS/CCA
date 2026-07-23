import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { MEASUREMENT_TYPE_TO_UNIT_MAP } from '@shared/pipes';
import { Improvement } from '@shared/types';
import { toNumber } from '@shared/utils';

import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

import { calculateOtherYearsVariableEnergy } from '../../underlying-agreement';
import { boolToString } from '../../utils';
import { isCarbonMeasurementType, resolveMeasurementUnit } from '../utils';

type TotalsOnlySummaryDataArgs = {
  referenceData: PerformanceDataFacilityReferenceData;
  performanceData: PerformanceDataFacilityInputData;
  targetVariableEnergy: number | null;
  isEditable: boolean;
};

export function toTPRBaselineDataDetails(
  referenceData: PerformanceDataFacilityReferenceData,
  productsLinkBasePath?: string,
): SummaryData {
  const datePipe = new GovukDatePipe();
  const decimalPipe = new DecimalPipe('en-GB');

  const basePath = productsLinkBasePath ?? '..';
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

  const isVariableEnergyAmount =
    baselineAndTargets.variableEnergyType === 'TOTALS' || baselineAndTargets.variableEnergyType === 'BY_PRODUCT';

  const factory = new SummaryFactory()
    .addSection('Details of baseline data')
    .addRow(
      'Is at least 12 months of consecutive baseline data available?',
      boolToString(baselineAndTargets?.isTwelveMonths),
    )
    .addRow('Start date of baseline', datePipe.transform(baselineAndTargets?.baselineDate))
    .addRow(
      'Must the Special Reporting Methodology (SRM) be applied for this facility?',
      boolToString(baselineAndTargets?.usedReportingMechanism),
    )
    .addRow(
      'Baseline energy to carbon dioxide factor (kgCO2e/kWh)',
      decimalPipe.transform(toNumber(baselineAndTargets?.energyCarbonFactor), '1.0-7'),
    )
    .addSection('Details of baseline energy or carbon')
    .addRow(
      `Fixed baseline energy for the facility (${MEASUREMENT_TYPE_TO_UNIT_MAP[baselineAndTargets?.measurementType]})`,
      `${decimalPipe.transform(baselineAndTargets?.totalFixedEnergy, '1.0-7')}`,
    )
    .addRow('Is there a variable energy amount?', boolToString(isVariableEnergyAmount))
    .addRow(
      'Indicate how you want to account for the portion of variable energy used (or carbon dioxide emitted) for your facility',
      baselineAndTargets.variableEnergyType === 'TOTALS'
        ? 'Totals only'
        : baselineAndTargets.variableEnergyType === 'BY_PRODUCT'
          ? 'Split by product'
          : '',
    )
    .addRow('Products submitted', productLabel, { link: productCount ? `${basePath}/products` : '' })
    .addRow(
      `Variable baseline energy for the facility (${MEASUREMENT_TYPE_TO_UNIT_MAP[baselineAndTargets?.measurementType]})`,
      decimalPipe.transform(baselineAndTargets?.baselineVariableEnergy, '1.0-7'),
    )
    .addRow(
      `Total baseline energy for the facility (${MEASUREMENT_TYPE_TO_UNIT_MAP[baselineAndTargets?.measurementType]})`,
      decimalPipe.transform(
        toNumber(baselineAndTargets?.totalFixedEnergy) + toNumber(baselineAndTargets?.baselineVariableEnergy),
        '1.0-7',
      ),
    );

  if (otherYearsVariableEnergy) {
    factory.addRow(
      `Other years - variable baseline energy (${MEASUREMENT_TYPE_TO_UNIT_MAP[baselineAndTargets?.measurementType]})`,
      decimalPipe.transform(otherYearsVariableEnergy, '1.0-7'),
    );
  }

  if (baselineAndTargets?.totalThroughput) {
    factory.addRow(
      `Total baseline throughput (${baselineAndTargets?.throughputUnit})`,
      decimalPipe.transform(baselineAndTargets?.totalThroughput, '1.0-7'),
    );
  }

  return factory
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

export function toTotalsOnlySummaryData(args: TotalsOnlySummaryDataArgs): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');

  const baselineAndTargets = args.referenceData?.baselineAndTargets;
  const measurementUnit = resolveMeasurementUnit(args.referenceData);
  const isCarbonMeasurement = isCarbonMeasurementType(measurementUnit);

  const factory = new SummaryFactory()
    .addSection('Target period throughput details', '../details')
    .addRow(
      `Total throughput (${baselineAndTargets?.throughputUnit})`,
      decimalPipe.transform(args.performanceData?.throughputDetails?.actualThroughput, '1.0-7'),
      { change: args.isEditable },
    );

  if (args.targetVariableEnergy !== null) {
    factory
      .addSection('Calculated energy amounts', '../details')
      .addRow(
        `Total target variable ${isCarbonMeasurement ? 'carbon dioxide' : 'energy'} (${measurementUnit})`,
        decimalPipe.transform(args.targetVariableEnergy, '1.0-7'),
      );
  }

  return factory.create();
}
