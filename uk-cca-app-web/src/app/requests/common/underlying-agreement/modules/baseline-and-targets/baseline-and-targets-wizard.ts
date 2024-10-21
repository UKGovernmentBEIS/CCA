import { TargetPeriod6Details } from 'cca-api';

import {
  isBaselineDataStepComplete,
  isTargetCompositionStepComplete,
  isTargetsStepComplete,
} from './baseline-and-targets-wizard-steps';

export const isTargetPeriodWizardCompleted = (
  isTargetPeriodFive: boolean,
  baselineExists: boolean,
  targetPeriodDetails?: TargetPeriod6Details,
): boolean => {
  if (isTargetPeriodFive && !baselineExists) return true;
  if (!targetPeriodDetails) return false;

  const targetCompositionStepComplete = isTargetCompositionStepComplete(targetPeriodDetails.targetComposition);
  const baselineDataStepComplete = isBaselineDataStepComplete(
    targetPeriodDetails.baselineData,
    targetPeriodDetails.targetComposition?.agreementCompositionType,
  );

  const targetsStepComplete = isTargetsStepComplete(
    targetPeriodDetails.targets?.improvement,
    targetPeriodDetails.targetComposition?.agreementCompositionType,
  );

  return targetCompositionStepComplete && baselineDataStepComplete && targetsStepComplete;
};
