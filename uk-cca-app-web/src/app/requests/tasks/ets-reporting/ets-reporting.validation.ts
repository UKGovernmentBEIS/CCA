import { toBigNumber } from '@shared/utils';
import BigNumber from 'bignumber.js';

import { EmissionsTradingSchemeDataInputDetails } from 'cca-api';

import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, EtsFuelRowInput, EtsReportingTaskPayload } from './ets-reporting.types';
import {
  createEtsFuelDetails,
  ETS_FUEL_ENTRIES,
  isEtsStandardFuelName,
  mapEtsStandardFuelRows,
} from './ets-reporting-calculations';

export function areEtsUseAnswersComplete(input?: EmissionsTradingSchemeDataInputDetails): boolean {
  return (
    input?.isUltraSmallEmitter === false ||
    (input?.isUltraSmallEmitter === true && typeof input.provideFuelDetails === 'boolean')
  );
}

export function requiresEtsFuelDetails(input?: EmissionsTradingSchemeDataInputDetails): boolean {
  return (
    input?.isUltraSmallEmitter === false || (input?.isUltraSmallEmitter === true && input.provideFuelDetails === true)
  );
}

/** Branch validity shared by completion, resume routing, the task list and submit protection. */
export function isEtsReportingComplete(payload?: EtsReportingTaskPayload): boolean {
  const input = payload?.emissionsTradingSchemeDataInputDetails;
  if (!areEtsUseAnswersComplete(input)) return false;
  if (!requiresEtsFuelDetails(input)) return input.energyFuelDetails == null;
  if (input.isUltraSmallEmitter === false && input.provideFuelDetails != null) return false;

  const details = input.energyFuelDetails;
  if (!details) return false;
  // Omitted collections use the API's empty defaults; explicit null collections do not.
  if (details.standardFuels === null || details.nonStandardFuels === null) return false;
  const standardEntries = Object.entries(details.standardFuels ?? {});
  const customFuels = details.nonStandardFuels ?? [];
  if (customFuels.length > 10 || standardEntries.length + customFuels.length === 0) return false;
  if (
    standardEntries.some(
      ([key, row]) =>
        !ETS_FUEL_ENTRIES.some(([allowed]) => allowed === key) ||
        !isValidEtsDecimal(row?.deliveredEnergy, true) ||
        !isValidEtsDecimal(row?.primaryEnergy),
    )
  )
    return false;

  const names = customFuels.map((fuel) => fuel?.name?.trim().toLowerCase());
  if (
    customFuels.some(
      (fuel, index) =>
        !names[index] ||
        isEtsStandardFuelName(fuel.name) ||
        fuel.name.length > 255 ||
        names.indexOf(names[index]) !== index ||
        !isValidEtsDecimal(fuel.conversionFactor) ||
        !isValidEtsDecimal(fuel.deliveredEnergy, true) ||
        !isValidEtsDecimal(fuel.primaryEnergy),
    )
  )
    return false;

  if (!isValidEtsDecimal(details.totalPrimaryEnergy) || !isValidEtsDecimal(details.totalCo2Emissions)) return false;
  const rows: EtsFuelRowInput[] = [
    ...mapEtsStandardFuelRows(payload.measurementType, details.standardFuels),
    ...customFuels.map<EtsFuelRowInput>((fuel) => ({
      fuelKey: null,
      fuelType: fuel.name,
      co2ConversionFactor: fuel.conversionFactor,
      deliveredEnergy: fuel.deliveredEnergy,
      primaryEnergyConversionFactor: '1',
      isCustom: true,
    })),
  ];
  const calculated = createEtsFuelDetails(payload.measurementType, rows);
  return (
    toBigNumber(details.totalPrimaryEnergy).eq(calculated.totalPrimaryEnergy) &&
    toBigNumber(details.totalCo2Emissions).eq(calculated.totalCo2Emissions) &&
    standardEntries.every(([key, row]) =>
      toBigNumber(row.primaryEnergy).eq(calculated.standardFuels[key].primaryEnergy),
    ) &&
    customFuels.every((fuel, index) =>
      toBigNumber(fuel.primaryEnergy).eq(calculated.nonStandardFuels[index].primaryEnergy),
    )
  );
}

export function canSubmitEtsReporting(payload?: EtsReportingTaskPayload): boolean {
  return (
    payload?.sectionsCompleted?.[ETS_ENERGY_FUEL_DETAILS_SUBTASK] === 'COMPLETED' && isEtsReportingComplete(payload)
  );
}

export function isValidEtsDecimal(value: string | null | undefined, positive = false): boolean {
  if (value == null || String(value).trim() === '') return false;
  try {
    const decimal = new BigNumber(value);
    return (
      decimal.isFinite() &&
      decimal.decimalPlaces() <= 7 &&
      (positive ? decimal.isGreaterThan(0) : decimal.isGreaterThanOrEqualTo(0))
    );
  } catch {
    return false;
  }
}
