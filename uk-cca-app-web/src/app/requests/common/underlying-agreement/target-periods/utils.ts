import BigNumber from 'bignumber.js';

import { TargetComposition } from 'cca-api';

import { transformMeasurementTypeToUnit } from '../pipes';

export function calculatePerformance(energyOrCarbon: string, throughput: string) {
  const energyOrCarbonBig = new BigNumber(energyOrCarbon);
  const throughputBig = new BigNumber(throughput);

  const calculation = energyOrCarbonBig.div(throughputBig).decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber();

  return !Number.isNaN(calculation) ? calculation : 0;
}

// For relative target types the baseline is the energy/throughput
export function calculateRelativeTarget(energy: string, throughput: string, improvement: string) {
  const energyBig = new BigNumber(energy);
  const throughputBig = new BigNumber(throughput);
  const improvementBig = new BigNumber(improvement);

  const performance = energyBig.div(throughputBig);

  const baseline = performance
    .multipliedBy(new BigNumber(100).minus(improvementBig).div(100))
    .decimalPlaces(7, BigNumber.ROUND_HALF_UP)
    .toNumber();

  return !Number.isNaN(baseline) ? baseline : 0;
}

export function calculateAbsoluteTarget(energyOrCarbon: string, improvement: string, tp5: boolean) {
  const energyOrCarbonBig = new BigNumber(energyOrCarbon);
  const improvementBig = new BigNumber(improvement);

  const baseline = energyOrCarbonBig.multipliedBy(new BigNumber(100).minus(improvementBig).div(100));

  return !Number.isNaN(baseline.toNumber())
    ? tp5
      ? baseline.multipliedBy(2).decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber()
      : baseline.decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber()
    : null;
}

export function getMeasurementAndThroughputUnits(
  targetThroughputUnit: TargetComposition['throughputUnit'],
  measurementType: TargetComposition['measurementType'],
) {
  const measurementTypeUnit = transformMeasurementTypeToUnit(measurementType);

  return `${measurementTypeUnit}/${targetThroughputUnit}`;
}

export function getBaselineUnits(
  targetThroughputUnit: TargetComposition['throughputUnit'],
  measurementType: TargetComposition['measurementType'],
  agreementCompositionType: TargetComposition['agreementCompositionType'],
) {
  switch (agreementCompositionType) {
    case 'NOVEM':
      return 'N/A';

    case 'ABSOLUTE':
      return transformMeasurementTypeToUnit(measurementType);

    case 'RELATIVE':
      return getMeasurementAndThroughputUnits(targetThroughputUnit, measurementType);
  }
}
