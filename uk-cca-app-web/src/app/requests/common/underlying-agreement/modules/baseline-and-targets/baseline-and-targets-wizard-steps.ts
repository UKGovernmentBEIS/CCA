import { BaselineData, TargetComposition } from 'cca-api';

export const isTargetCompositionStepComplete = (targetComposition?: TargetComposition): boolean => {
  if (!targetComposition) {
    return false;
  }
  // mandatory fields
  if (
    !targetComposition.calculatorFile ||
    !targetComposition.measurementType ||
    !targetComposition.agreementCompositionType
  )
    return false;

  const isNovem = targetComposition.agreementCompositionType === 'NOVEM';

  if (isNovem) return true;
  if (!targetComposition.isTargetUnitThroughputMeasured) return true;

  // check if novem fields are populated
  const areConditionalFieldsComplete =
    !!targetComposition.throughputUnit && !!targetComposition.conversionEvidences?.length;

  if (areConditionalFieldsComplete) return true;
  return false;
};

export const isBaselineDataStepComplete = (
  baselineData: BaselineData,
  agreementCompositionType: TargetComposition['agreementCompositionType'],
): boolean => {
  if (!baselineData) return false;

  const { isTwelveMonths, baselineDate, energy, usedReportingMechanism, throughput, energyCarbonFactor } = baselineData;

  if (typeof isTwelveMonths !== 'boolean') return false;
  if (!baselineDate) return false;
  const hasValidExplanation = explanationValid(baselineData);
  if (!hasValidExplanation) return false;

  if (!energy || !energyCarbonFactor) return false;

  if (agreementCompositionType !== 'NOVEM' && (!throughput || typeof usedReportingMechanism !== 'boolean'))
    return false;

  return true;
};

export const isTargetsStepComplete = (
  improvement: number,
  agreementCompositionType: TargetComposition['agreementCompositionType'],
): boolean => {
  if (!agreementCompositionType) return false;
  return typeof improvement === 'number' || typeof improvement === 'string';
};

function explanationValid(baselineData: BaselineData): boolean {
  if (!baselineData.isTwelveMonths) return !!baselineData.explanation;
  const baselineConditionDate = new Date(baselineData.baselineDate);
  const isExplanationRequired =
    baselineConditionDate && baselineConditionDate.getTime() !== new Date('2018-01-01').getTime();
  if (isExplanationRequired && !baselineData.explanation) return false;
  return true;
}
