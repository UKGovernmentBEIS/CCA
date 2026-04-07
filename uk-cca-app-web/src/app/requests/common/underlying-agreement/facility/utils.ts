import BigNumber from 'bignumber.js';

import { ProductVariableEnergyConsumptionData } from 'cca-api';

export function calculateEnergyConsumedEligible(energyConsumed: number, energyConsumedProvision: number): number {
  const energyConsumedBig = new BigNumber(energyConsumed);
  const energyConsumedProvisionBig = new BigNumber(energyConsumedProvision);

  return energyConsumedBig
    .multipliedBy(energyConsumedProvisionBig)
    .div(100)
    .plus(energyConsumedBig)
    .decimalPlaces(7, BigNumber.ROUND_HALF_UP)
    .toNumber();
}

// Normalises user entered numbers that may be stored as strings (including scientific), empty values or undefined
export function normaliseNumber(field: string | number | null | undefined): number | null {
  return Number.isNaN(Number(field)) ? null : Number(field);
}

export function calculateFixedEnergy(totalFixedEnergy: string | number | null | undefined): string | null {
  return normaliseNumber(totalFixedEnergy)?.toString() ?? null;
}

export function calculateVariableEnergy(
  hasVariableEnergy?: boolean | null,
  variableEnergyType?: string | null | undefined,
  baselineVariableEnergy?: string | number | null | undefined,
  products?: ProductVariableEnergyConsumptionData[] | null | undefined,
  facilityBaselineYear?: number | null | undefined,
): string | null {
  if (!hasVariableEnergy) return '0';

  if (variableEnergyType === 'TOTALS') {
    return normaliseNumber(baselineVariableEnergy)?.toString() ?? null;
  }

  if (variableEnergyType === 'BY_PRODUCT') {
    const sum = (products ?? [])
      .filter((product) => (product.productStatus ?? '').toUpperCase() !== 'EXCLUDED')
      .filter((product) => Number(product.baselineYear) === facilityBaselineYear)
      .reduce((acc, product) => acc + normaliseNumber(product.energy), 0);

    return sum.toString();
  }

  return '0';
}

export function calculateTotalEnergy(
  totalFixedEnergy: string | number | null | undefined,
  totalVariableEnergy: string | number | null | undefined,
): string | null {
  if (totalFixedEnergy == null || totalVariableEnergy == null) return null;
  const fixed = normaliseNumber(totalFixedEnergy);
  const variable = normaliseNumber(totalVariableEnergy);
  if (fixed === null || variable === null) return null;
  return (fixed + variable).toString();
}

export function calculateOtherYearsVariableEnergy(
  products: ProductVariableEnergyConsumptionData[] | null | undefined,
  facilityBaselineYear: number | null | undefined,
  variableEnergyType?: string | null | undefined,
): string | null {
  if (variableEnergyType !== 'BY_PRODUCT') {
    return null;
  }

  const sum = (products ?? [])
    .filter((product) => (product.productStatus ?? '').toUpperCase() !== 'EXCLUDED')
    .filter((product) => Number(product.baselineYear) !== facilityBaselineYear)
    .reduce((acc, product) => acc + normaliseNumber(product.energy), 0);

  return sum > 0 ? sum.toString() : null;
}
