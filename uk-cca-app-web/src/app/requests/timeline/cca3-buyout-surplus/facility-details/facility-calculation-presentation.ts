import BigNumber from 'bignumber.js';

import { HistoricalFacility } from '../historical-facilities';
import { FacilityCalculationPresentation } from './facility-calculation.types';

/** Select the financial rows from the saved result without gating on inferred history. */
export function toFacilityCalculationPresentation(
  facility: HistoricalFacility,
): FacilityCalculationPresentation | undefined {
  const targetMet = facility.performanceOutcome === 'TARGET_MET';
  if (!targetMet && facility.performanceOutcome !== 'TARGET_NOT_MET') return undefined;
  if (facility.submissionType !== 'PRIMARY' && facility.submissionType !== 'SECONDARY') return undefined;

  // Use the recorded calculation rules for primary/secondary layouts, alongside the
  // saved amounts that identify refunds and previous adjustments.
  if (facility.chargeType === 'REFUND') {
    return targetMet
      ? { scenario: 'secondary-target-met-refund' }
      : { scenario: 'secondary-target-not-met-refund', grossBuyoutFee: grossBuyoutFee(facility) };
  }

  const previousSurplus = isPositive(facility.previousSurplusGained);
  const previousPayments = isPositive(facility.previousPaidFees) || isPositive(facility.previousSurplusUsed);
  if (targetMet) {
    return {
      scenario:
        !facility.primaryRulesApplied || previousSurplus || previousPayments ? 'secondary-surplus' : 'primary-surplus',
    };
  }

  if (!isPositive(facility.surplusUsed)) {
    if (previousSurplus) {
      return { scenario: 'secondary-buyout-after-surplus', grossBuyoutFee: grossBuyoutFee(facility) };
    }
    if (!facility.primaryRulesApplied || previousPayments) {
      return { scenario: 'secondary-buyout-after-buyout', grossBuyoutFee: grossBuyoutFee(facility) };
    }
  }

  return {
    scenario: 'primary-buyout',
    showTransaction: facility.chargeType === 'FEE' && isPositive(facility.invoicedBuyOutFee),
  };
}

/** The API's settlement is a positive fee/refund magnitude, after earlier payments. */
function grossBuyoutFee(facility: HistoricalFacility): string | null {
  const paid = parseAmount(facility.previousPaidFees);
  const settlement = parseAmount(facility.invoicedBuyOutFee);
  if (!paid || !settlement || paid.isNegative() || settlement.isNegative()) return null;
  if (facility.chargeType == null && !settlement.isZero()) return null;

  const total = facility.chargeType === 'REFUND' ? paid.minus(settlement) : paid.plus(settlement);
  return total.isNegative() ? null : total.toFixed();
}

function isPositive(amount: string | number | null | undefined): boolean {
  return parseAmount(amount)?.isGreaterThan(0) ?? false;
}

function parseAmount(amount: string | number | null | undefined): BigNumber | undefined {
  if (amount == null) return undefined;
  try {
    const value = new BigNumber(amount);
    return value.isFinite() ? value : undefined;
  } catch {
    return undefined;
  }
}
