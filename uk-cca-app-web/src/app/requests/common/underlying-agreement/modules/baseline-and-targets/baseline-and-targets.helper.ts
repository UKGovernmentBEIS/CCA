import { Observable, of } from 'rxjs';

import { transformFilesToAttachments, transformFilesToUUIDsList } from '@shared/utils';
import BigNumber from 'bignumber.js';
import produce from 'immer';

import { AccountReferenceData, SectorAssociationDetails, TargetComposition, Targets } from 'cca-api';

import { TaskItemStatus } from '../../../task-item-status';
import { transformMeasurementTypeToUnit } from '../../pipes';
import {
  BaseLineAndTargetsReviewStep,
  BaseLineAndTargetsStep,
  BaselineDataUserInput,
  TargetCompositionUserInput,
  TargetPeriodExistUserInput,
  UNARequestTaskPayload,
} from '../../underlying-agreement.types';

export const roundDecimals = (n: number, decimals = 3): number => new BigNumber(n).decimalPlaces(decimals).toNumber();

export function calculatePerformance(energyOrCarbon: number, throughput: number) {
  const energyOrCarbonBig = new BigNumber(energyOrCarbon);
  const throughputBig = new BigNumber(throughput);
  const calculation = energyOrCarbonBig.div(throughputBig).decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber();

  return !isNaN(calculation) ? calculation : null;
}

// For relative target types the baseline is the energy/throughput
export function calculateRelativeTarget(performance: number, improvement: number) {
  const performanceBig = new BigNumber(performance);
  const improvementBig = new BigNumber(improvement);

  const baseline = performanceBig
    .multipliedBy(new BigNumber(100).minus(improvementBig).div(100))
    .decimalPlaces(7, BigNumber.ROUND_HALF_UP)
    .toNumber();

  return !isNaN(baseline) ? baseline : null;
}

export function calculateAbsoluteTarget(energyOrCarbon: number, improvement: number, extendedPeriod: boolean) {
  const energyOrCarbonBig = new BigNumber(energyOrCarbon);
  const improvementBig = new BigNumber(improvement);
  const baseline = energyOrCarbonBig.multipliedBy(new BigNumber(100).minus(improvementBig).div(100));

  return !isNaN(baseline.toNumber())
    ? extendedPeriod
      ? baseline.multipliedBy(2).decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber()
      : baseline.decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber()
    : null;
}

export function getMeasurementAndThroughputUnits(
  targetThroughputUnit: TargetComposition['throughputUnit'],
  sectorThroughputUnit: AccountReferenceData['sectorAssociationDetails']['throughputUnit'],
  measurementType: TargetComposition['measurementType'],
) {
  const measurementTypeUnit = transformMeasurementTypeToUnit(measurementType);
  const throughputUnit = targetThroughputUnit ?? sectorThroughputUnit;

  return `${measurementTypeUnit}/${throughputUnit}`;
}

export function getBaselineUnits(
  targetThroughputUnit: TargetComposition['throughputUnit'],
  sectorThroughputUnit: SectorAssociationDetails['throughputUnit'],
  measurementType: TargetComposition['measurementType'],
  agreementCompositionType: TargetComposition['agreementCompositionType'],
) {
  switch (agreementCompositionType) {
    case 'NOVEM':
      return 'N/A';

    case 'ABSOLUTE':
      return transformMeasurementTypeToUnit(measurementType);

    case 'RELATIVE':
      return getMeasurementAndThroughputUnits(targetThroughputUnit, sectorThroughputUnit, measurementType);
  }
}

export function initializeTP5Details(payload: UNARequestTaskPayload) {
  if (!payload.underlyingAgreement.targetPeriod5Details.details) {
    payload.underlyingAgreement.targetPeriod5Details.details = {
      targetComposition: null,
      baselineData: null,
      targets: null,
    };

    payload.underlyingAgreement.targetPeriod5Details.details.targetComposition = {
      calculatorFile: null,
      measurementType: null,
      agreementCompositionType: null,
      isTargetUnitThroughputMeasured: null,
      throughputUnit: null,
      conversionFactor: null,
      conversionEvidences: [],
    };

    payload.underlyingAgreement.targetPeriod5Details.details.baselineData = {
      isTwelveMonths: null,
      baselineDate: null,
      explanation: null,
      greenfieldEvidences: [],
      energy: null,
      usedReportingMechanism: null,
      throughput: null,
      performance: null,
      energyCarbonFactor: null,
    };

    payload.underlyingAgreement.targetPeriod5Details.details.targets = {
      improvement: null,
      target: null,
    };
  }
}

export function targetPeriod5NextStepPath(currentStep: string, targetPeriodExist: boolean): Observable<string> {
  switch (currentStep) {
    case BaseLineAndTargetsStep.BASELINE_EXISTS:
      return targetPeriodExist
        ? of('../' + BaseLineAndTargetsStep.TARGET_COMPOSITION)
        : of('../' + BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS);

    case BaseLineAndTargetsStep.TARGET_COMPOSITION:
      return of('../' + BaseLineAndTargetsStep.ADD_BASELINE_DATA);

    case BaseLineAndTargetsStep.ADD_BASELINE_DATA:
      return of('../' + BaseLineAndTargetsStep.ADD_TARGETS);

    case BaseLineAndTargetsStep.ADD_TARGETS:
      return of('../' + BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS);
  }
}

export function targetPeriod5ReviewNextStepPath(currentStep: string, baselineExists: boolean): Observable<string> {
  switch (currentStep) {
    case BaseLineAndTargetsReviewStep.BASELINE_EXISTS:
      return of(
        '../' +
          (baselineExists ? BaseLineAndTargetsReviewStep.TARGET_COMPOSITION : BaseLineAndTargetsReviewStep.DECISION),
      );
    case BaseLineAndTargetsReviewStep.TARGET_COMPOSITION:
      return of('../' + BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA);

    case BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA:
      return of('../' + BaseLineAndTargetsReviewStep.ADD_TARGETS);

    case BaseLineAndTargetsReviewStep.ADD_TARGETS:
      return of('../' + BaseLineAndTargetsReviewStep.DECISION);

    case BaseLineAndTargetsReviewStep.DECISION:
      return of('../' + BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS);

    case BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS:
      return of('../' + BaseLineAndTargetsReviewStep.SUMMARY);

    default:
      return of('../' + BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS);
  }
}

export function targetPeriod6NextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case BaseLineAndTargetsStep.TARGET_COMPOSITION:
      return of('../' + BaseLineAndTargetsStep.ADD_BASELINE_DATA);

    case BaseLineAndTargetsStep.ADD_BASELINE_DATA:
      return of('../' + BaseLineAndTargetsStep.ADD_TARGETS);

    case BaseLineAndTargetsStep.ADD_TARGETS:
      return of('../' + BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS);
  }
}

export function targetPeriod6ReviewNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case BaseLineAndTargetsReviewStep.TARGET_COMPOSITION:
      return of('../' + BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA);

    case BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA:
      return of('../' + BaseLineAndTargetsReviewStep.ADD_TARGETS);

    case BaseLineAndTargetsReviewStep.ADD_TARGETS:
      return of('../' + BaseLineAndTargetsReviewStep.DECISION);

    case BaseLineAndTargetsReviewStep.DECISION:
      return of('../' + BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS);

    case BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS:
      return of('../' + BaseLineAndTargetsReviewStep.SUMMARY);
  }
}

export function applyTp5ExistSideEffect(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;

      if (!payload.underlyingAgreement.targetPeriod5Details.exist) {
        payload.underlyingAgreement.targetPeriod5Details.details = null;
      } else {
        initializeTP5Details(payload);
      }
    }),
  );
}

export function applyTp5BaselineExists(
  currentPayload: UNARequestTaskPayload,
  userInput: TargetPeriodExistUserInput,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.underlyingAgreement.targetPeriod5Details.exist = userInput.exist;
    }),
  );
}

export function applyTp5TargetComposition(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: TargetCompositionUserInput,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;

      const attachments = transformFilesToAttachments([
        userInput.calculatorFile,
        ...(userInput?.conversionEvidences || []),
      ]);

      payload.underlyingAgreement.targetPeriod5Details.details.targetComposition = {
        ...userInput,
        conversionFactor: round(userInput.conversionFactor, 4),
        conversionEvidences: transformFilesToUUIDsList(userInput.conversionEvidences) as string[],
        calculatorFile: transformFilesToUUIDsList(userInput.calculatorFile) as string,
      };

      payload.underlyingAgreementAttachments = { ...currentPayload.underlyingAgreementAttachments, ...attachments };
    }),
  );
}

export function applyTp6TargetComposition(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: TargetCompositionUserInput,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;

      const attachments = transformFilesToAttachments([
        userInput.calculatorFile,
        ...(userInput?.conversionEvidences || []),
      ]);

      payload.underlyingAgreement.targetPeriod6Details.targetComposition = {
        ...userInput,
        conversionFactor: round(userInput.conversionFactor, 4),
        conversionEvidences: (transformFilesToUUIDsList(userInput.conversionEvidences) as string[]) ?? [],
        calculatorFile: transformFilesToUUIDsList(userInput.calculatorFile) as string,
      };

      payload.underlyingAgreementAttachments = { ...currentPayload.underlyingAgreementAttachments, ...attachments };
    }),
  );
}

export function applyTp5BaselineData(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: BaselineDataUserInput,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;

      const hasRelativeAgreementCompositionType =
        payload.underlyingAgreement.targetPeriod5Details.details.targetComposition.agreementCompositionType ===
        'RELATIVE';
      const attachments = transformFilesToAttachments(userInput?.greenfieldEvidences ?? []);

      payload.underlyingAgreement.targetPeriod5Details.details.baselineData = {
        ...userInput,
        energy: round(userInput.energy),
        throughput: round(userInput.throughput),
        performance: hasRelativeAgreementCompositionType
          ? calculatePerformance(userInput.energy, userInput.throughput)
          : null,
        energyCarbonFactor: round(userInput.energyCarbonFactor, 4),
        greenfieldEvidences: transformFilesToUUIDsList(userInput?.greenfieldEvidences) as string[],
      };

      payload.underlyingAgreementAttachments = { ...currentPayload.underlyingAgreementAttachments, ...attachments };
    }),
  );
}

export function applyTp6BaselineData(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: BaselineDataUserInput,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;

      const hasRelativeAgreementCompositionType =
        payload.underlyingAgreement.targetPeriod6Details.targetComposition.agreementCompositionType === 'RELATIVE';

      const attachments = transformFilesToAttachments(userInput?.greenfieldEvidences ?? []);

      payload.underlyingAgreement.targetPeriod6Details.baselineData = {
        ...userInput,
        energy: round(userInput.energy),
        throughput: round(userInput.throughput),
        performance: hasRelativeAgreementCompositionType
          ? calculatePerformance(userInput.energy, userInput.throughput)
          : null,
        energyCarbonFactor: round(userInput.energyCarbonFactor, 4),
        greenfieldEvidences: transformFilesToUUIDsList(userInput?.greenfieldEvidences) as string[],
      };

      payload.underlyingAgreementAttachments = { ...currentPayload.underlyingAgreementAttachments, ...attachments };
    }),
  );
}

export function applyTp5AddTargets(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: Targets,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      payload.underlyingAgreement.targetPeriod5Details.details.targets = {
        ...userInput,
      };
    }),
  );
}

export function applyTp6AddTargets(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: Targets,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      payload.underlyingAgreement.targetPeriod6Details.targets = {
        ...userInput,
      };
    }),
  );
}

export function applyTp5BaselineDataSideEffect(
  currentPayload: UNARequestTaskPayload,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      const targetComposition = payload.underlyingAgreement.targetPeriod5Details?.details?.targetComposition;
      const baselineData = payload.underlyingAgreement.targetPeriod5Details?.details?.baselineData;
      const targets = payload.underlyingAgreement.targetPeriod5Details?.details?.targets;
      const agreementCompositionType = targetComposition?.agreementCompositionType;
      const performance = baselineData?.performance;
      const energyOrCarbon = baselineData?.energy;
      const improvement = targets?.improvement;

      if (!improvement) return;

      switch (agreementCompositionType) {
        case 'RELATIVE':
          payload.underlyingAgreement.targetPeriod5Details.details.targets.target = calculateRelativeTarget(
            performance,
            improvement,
          );
          return;

        case 'ABSOLUTE':
          payload.underlyingAgreement.targetPeriod5Details.details.targets.target = calculateAbsoluteTarget(
            energyOrCarbon,
            improvement,
            false,
          );
          return;
      }
    }),
  );
}

export function applyTp6BaselineDataSideEffect(
  currentPayload: UNARequestTaskPayload,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      const targetComposition = payload.underlyingAgreement.targetPeriod6Details?.targetComposition;
      const baselineData = payload.underlyingAgreement.targetPeriod6Details?.baselineData;
      const targets = payload.underlyingAgreement.targetPeriod6Details?.targets;
      const agreementCompositionType = targetComposition.agreementCompositionType;
      const performance = baselineData?.performance;
      const energyOrCarbon = baselineData?.energy;
      const improvement = targets?.improvement;

      if (!improvement) return;

      switch (agreementCompositionType) {
        case 'RELATIVE':
          payload.underlyingAgreement.targetPeriod6Details.targets.target = calculateRelativeTarget(
            performance,
            improvement,
          );
          return;

        case 'ABSOLUTE':
          payload.underlyingAgreement.targetPeriod6Details.targets.target = calculateAbsoluteTarget(
            energyOrCarbon,
            improvement,
            false,
          );
          return;
      }
    }),
  );
}

export function applyTp5TargetCompositionSideEffect(
  currentPayload: UNARequestTaskPayload,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      const agreementCompositionType =
        payload.underlyingAgreement.targetPeriod5Details.details.targetComposition.agreementCompositionType;
      const baselineData = payload.underlyingAgreement.targetPeriod5Details.details.baselineData;
      const targets = payload.underlyingAgreement.targetPeriod5Details.details.targets;

      const energy = baselineData?.energy;
      const throughput = baselineData?.throughput;
      const improvement = targets?.improvement;

      if (agreementCompositionType !== 'RELATIVE')
        delete payload.underlyingAgreement.targetPeriod5Details.details?.baselineData?.performance;

      if (!targets) return;

      // update target if respective baselineData exists
      switch (agreementCompositionType) {
        case 'NOVEM':
          delete payload.underlyingAgreement.targetPeriod5Details.details?.targets?.target;
          break;

        case 'RELATIVE':
          if (!energy || !throughput) return;

          payload.underlyingAgreement.targetPeriod5Details.details.targets.target = calculateRelativeTarget(
            calculatePerformance(energy, throughput),
            improvement,
          );
          break;

        case 'ABSOLUTE':
          if (!energy || !throughput) return;

          payload.underlyingAgreement.targetPeriod5Details.details.targets.target = calculateAbsoluteTarget(
            energy,
            improvement,
            false,
          );
          break;
      }
    }),
  );
}

export function applyTp6TargetCompositionSideEffect(
  currentPayload: UNARequestTaskPayload,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      const agreementCompositionType =
        payload.underlyingAgreement.targetPeriod6Details.targetComposition.agreementCompositionType;
      const baselineData = payload.underlyingAgreement.targetPeriod6Details.baselineData;
      const targets = payload.underlyingAgreement.targetPeriod6Details.targets;

      const energy = baselineData?.energy;
      const throughput = baselineData?.throughput;
      const improvement = targets?.improvement;

      if (agreementCompositionType !== 'RELATIVE')
        delete payload.underlyingAgreement.targetPeriod6Details?.baselineData?.performance;

      if (!targets) return;

      // update target if respective baselineData exists
      switch (agreementCompositionType) {
        case 'NOVEM':
          delete payload.underlyingAgreement.targetPeriod6Details?.targets?.target;
          break;

        case 'RELATIVE':
          if (!energy || !throughput) return;

          payload.underlyingAgreement.targetPeriod6Details.targets.target = calculateRelativeTarget(
            calculatePerformance(energy, throughput),
            improvement,
          );
          break;

        case 'ABSOLUTE':
          if (!energy || !throughput) return;

          payload.underlyingAgreement.targetPeriod6Details.targets.target = calculateAbsoluteTarget(
            energy,
            improvement,
            false,
          );
          break;
      }
    }),
  );
}

function round(v: number | undefined, decimals = 3): number | null {
  return v ? roundDecimals(v, decimals) : null;
}
