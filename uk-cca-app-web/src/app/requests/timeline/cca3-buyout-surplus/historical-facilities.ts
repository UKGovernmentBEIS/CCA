import BigNumber from 'bignumber.js';

import {
  BuyOutSurplusCalculationDataContainer,
  BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload,
  BuyOutSurplusFacilityPerformanceDetail,
} from 'cca-api';

/** Display data from one historical action; amounts retain the API's decimal strings. */
export type HistoricalFacility = Partial<BuyOutSurplusCalculationDataContainer> &
  Pick<
    BuyOutSurplusFacilityPerformanceDetail,
    'facilityBusinessId' | 'siteName' | 'submissionType' | 'transactionCode' | 'primaryRulesApplied'
  > & {
    targetPeriod: BuyOutSurplusFacilityPerformanceDetail['targetPeriodType'];
    version: number;
    buyOutCost?: string;
  };

export function toHistoricalFacilities(
  payload: BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload | undefined,
): HistoricalFacility[] {
  // Normalize equal decimals without losing pennies on large historical costs.
  const costs = new Map<HistoricalFacility['targetPeriod'], Map<string, string>>();
  for (const summary of payload?.summary ?? []) {
    // Stored actions may predate the decimal string contract and still hold a number.
    const rawCost = summary.targetPeriodBuyOutCost == null ? '' : String(summary.targetPeriodBuyOutCost).trim();
    if (!rawCost) continue;
    try {
      const cost = new BigNumber(rawCost);
      if (!cost.isFinite() || cost.lt(0)) continue;
      const periodCosts = costs.get(summary.targetPeriodType) ?? new Map<string, string>();
      if (!periodCosts.has(cost.toFixed())) periodCosts.set(cost.toFixed(), rawCost);
      costs.set(summary.targetPeriodType, periodCosts);
    } catch {
      // Ignore malformed costs in historical data.
      continue;
    }
  }

  return (payload?.facilities ?? []).map((facility) => {
    const periodCosts = costs.get(facility.targetPeriodType);
    return {
      ...facility.data,
      facilityBusinessId: facility.facilityBusinessId,
      siteName: facility.siteName,
      targetPeriod: facility.targetPeriodType,
      version: facility.performanceDataReportVersion,
      submissionType: facility.submissionType,
      primaryRulesApplied: facility.primaryRulesApplied,
      transactionCode: facility.transactionCode,
      // Fee and refund summaries share the recorded TP cost. Missing or conflicting
      // costs remain blank; today's configured cost cannot describe a past calculation.
      buyOutCost: periodCosts?.size === 1 ? periodCosts.values().next().value : undefined,
    };
  });
}
