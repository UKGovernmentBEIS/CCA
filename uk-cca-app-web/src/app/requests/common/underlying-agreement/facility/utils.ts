import BigNumber from 'bignumber.js';

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
