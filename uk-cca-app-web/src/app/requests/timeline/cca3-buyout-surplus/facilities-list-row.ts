import { formatGbp, getFacilityPerformanceReportStatusLabel } from '@shared/utils';
import BigNumber from 'bignumber.js';

import { toFacilityCalculationPresentation } from './facility-details/facility-calculation-presentation';
import { formatHistoricalNumber } from './format-historical-number';
import { HistoricalFacility } from './historical-facilities';

export interface FacilitiesListRow {
  siteName: string;
  facilityBusinessId: string;
  targetPeriod: string;
  targetPeriodId: HistoricalFacility['targetPeriod'];
  result: string;
  surplusUsed: string;
  buyoutAmount: string;
  refundAmount: string;
  surplusGained: string;
  detailsAvailable: boolean;
}

export function toFacilitiesListRow(facility: HistoricalFacility): FacilitiesListRow {
  // A calculated amount alone does not establish that a buy-out/refund transaction was issued.
  const hasTransaction = !!facility.transactionCode?.trim();
  // Only a target shortfall can consume surplus. Keep an explicit zero, but leave
  // absent or negative values blank rather than presenting them as surplus used.
  const showSurplusUsed =
    facility.performanceOutcome === 'TARGET_NOT_MET' &&
    facility.surplusUsed != null &&
    new BigNumber(facility.surplusUsed).isGreaterThanOrEqualTo(0);

  return {
    siteName: facility.siteName,
    facilityBusinessId: facility.facilityBusinessId,
    targetPeriod: facility.targetPeriod.slice(2),
    targetPeriodId: facility.targetPeriod,
    result: getFacilityPerformanceReportStatusLabel(facility.performanceOutcome),
    surplusUsed: showSurplusUsed ? formatHistoricalNumber(facility.surplusUsed) : '',
    buyoutAmount: hasTransaction && facility.chargeType === 'FEE' ? formatGbp(facility.invoicedBuyOutFee) : '',
    refundAmount: hasTransaction && facility.chargeType === 'REFUND' ? formatGbp(facility.invoicedBuyOutFee) : '',
    surplusGained: facility.performanceOutcome === 'TARGET_MET' ? formatHistoricalNumber(facility.surplusGained) : '',
    detailsAvailable: !!toFacilityCalculationPresentation(facility),
  };
}
