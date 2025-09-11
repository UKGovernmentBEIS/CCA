import { produce } from 'immer';

import { TargetPeriod5Details, TargetPeriod6Details } from 'cca-api';

import { calculateAbsoluteTarget, calculatePerformance, calculateRelativeTarget } from './utils';

export type HasTp5Details = { targetPeriod5Details?: TargetPeriod5Details };
export type HasTp6Details = { targetPeriod6Details?: TargetPeriod6Details };

/**
 * Applies business logic side effects for Target Period 5 baseline existence.
 *
 * Business Logic:
 * - If baseline does NOT exist: clears all TP5 details (set to null)
 * - If baseline EXISTS: initializes empty TP5 structure if not already present
 *
 * @param payload The payload to apply side effects to
 * @returns The updated payload with side effects applied
 */
export function applyTp5ExistSideEffect<T extends HasTp5Details>(payload: T): T {
  return produce(payload, (draft) => {
    if (!draft.targetPeriod5Details.exist) {
      draft.targetPeriod5Details.details = null;
    } else {
      if (!draft.targetPeriod5Details.details) {
        draft.targetPeriod5Details.details = {
          targetComposition: null,
          baselineData: null,
          targets: null,
        };
      }
    }
  });
}

/**
 * Applies business logic side effects for Target Period 5 target composition changes.
 *
 * Business Logic:
 * - NOVEM: removes target calculation (no targets needed)
 * - RELATIVE: calculates target as baseline performance * (100-improvement)/100
 * - ABSOLUTE: calculates target as energy * (100-improvement)/100
 * - Removes performance metric for non-RELATIVE agreement types
 *
 * @param payload The payload to apply side effects to
 * @returns The updated payload with side effects applied
 */
export function applyTp5TargetCompositionSideEffect<T extends HasTp5Details>(payload: T): T {
  return produce(payload, (draft) => {
    const agreementType = draft.targetPeriod5Details.details?.targetComposition?.agreementCompositionType;
    const baselineData = draft.targetPeriod5Details.details?.baselineData;
    const targets = draft.targetPeriod5Details.details?.targets;

    // Remove performance for non-RELATIVE agreements
    if (agreementType !== 'RELATIVE' && baselineData) baselineData.performance = null;

    if (!targets) return;

    const energy = baselineData?.energy;
    const throughput = baselineData?.throughput;
    const improvement = targets?.improvement;

    // Update target if respective baselineData exists
    switch (agreementType) {
      case 'NOVEM':
        targets.target = null;
        break;

      case 'RELATIVE':
        if (energy && throughput && improvement !== null && improvement !== undefined) {
          targets.target = calculateRelativeTarget(energy, throughput, improvement);
          baselineData.performance = calculatePerformance(energy, throughput);
        }
        break;

      case 'ABSOLUTE':
        if (energy && improvement !== null && improvement !== undefined) {
          targets.target = calculateAbsoluteTarget(energy, improvement, true);
        }
        break;
    }
  });
}

/**
 * Applies business logic side effects for Target Period 5 baseline data changes.
 *
 * Business Logic:
 * - Recalculates performance for RELATIVE agreements (energy/throughput)
 * - Recalculates targets when baseline data changes:
 *   - RELATIVE: uses energy/throughput ratio with improvement percentage
 *   - ABSOLUTE: uses energy value with improvement percentage
 *
 * @param payload The payload to apply side effects to
 * @returns The updated payload with side effects applied
 */
export function applyTp5BaselineDataSideEffect<T extends HasTp5Details>(payload: T): T {
  return produce(payload, (draft) => {
    const targetComposition = draft.targetPeriod5Details?.details?.targetComposition;
    const baselineData = draft.targetPeriod5Details?.details?.baselineData;
    const targets = draft.targetPeriod5Details?.details?.targets;
    const agreementCompositionType = targetComposition?.agreementCompositionType;

    const energyOrCarbon = baselineData?.energy;
    const improvement = targets?.improvement;

    if (!improvement) return;

    switch (agreementCompositionType) {
      case 'RELATIVE':
        if (energyOrCarbon && baselineData?.throughput) {
          targets.target = calculateRelativeTarget(energyOrCarbon, baselineData.throughput, improvement);
          baselineData.performance = calculatePerformance(energyOrCarbon, baselineData.throughput);
        }
        break;

      case 'ABSOLUTE':
        if (energyOrCarbon) {
          targets.target = calculateAbsoluteTarget(energyOrCarbon, improvement, true);
        }
        break;
    }
  });
}

/**
 * Applies business logic side effects for Target Period 6 target composition changes.
 * Same logic as TP5 but for Target Period 6.
 */
export function applyTp6TargetCompositionSideEffect<T extends HasTp6Details>(payload: T): T {
  return produce(payload, (draft) => {
    const agreementType = draft.targetPeriod6Details?.targetComposition?.agreementCompositionType;
    const baselineData = draft.targetPeriod6Details?.baselineData;
    const targets = draft.targetPeriod6Details?.targets;

    // Remove performance for non-RELATIVE agreements
    if (agreementType !== 'RELATIVE' && baselineData) baselineData.performance = null;

    if (!targets) return;

    const energy = baselineData?.energy;
    const throughput = baselineData?.throughput;
    const improvement = targets?.improvement;

    // Update target if respective baselineData exists
    switch (agreementType) {
      case 'NOVEM':
        targets.target = null;
        break;

      case 'RELATIVE':
        if (energy && throughput && improvement !== null && improvement !== undefined) {
          targets.target = calculateRelativeTarget(energy, throughput, improvement);
          baselineData.performance = calculatePerformance(energy, throughput);
        }
        break;

      case 'ABSOLUTE':
        if (energy && improvement !== null && improvement !== undefined) {
          targets.target = calculateAbsoluteTarget(energy, improvement, false);
        }
        break;
    }
  });
}

/**
 * Applies business logic side effects for Target Period 6 baseline data changes.
 * Same logic as TP5 but for Target Period 6.
 */
export function applyTp6BaselineDataSideEffect<T extends HasTp6Details>(payload: T): T {
  return produce(payload, (draft) => {
    const targetComposition = draft.targetPeriod6Details?.targetComposition;
    const baselineData = draft.targetPeriod6Details?.baselineData;
    const targets = draft.targetPeriod6Details?.targets;
    const agreementCompositionType = targetComposition?.agreementCompositionType;

    const energyOrCarbon = baselineData?.energy;
    const improvement = targets?.improvement;

    if (!improvement) return;

    switch (agreementCompositionType) {
      case 'RELATIVE':
        if (energyOrCarbon && baselineData?.throughput) {
          targets.target = calculateRelativeTarget(energyOrCarbon, baselineData.throughput, improvement);
          baselineData.performance = calculatePerformance(energyOrCarbon, baselineData.throughput);
        }
        break;

      case 'ABSOLUTE':
        if (energyOrCarbon) {
          targets.target = calculateAbsoluteTarget(energyOrCarbon, improvement, false);
        }
        break;
    }
  });
}
