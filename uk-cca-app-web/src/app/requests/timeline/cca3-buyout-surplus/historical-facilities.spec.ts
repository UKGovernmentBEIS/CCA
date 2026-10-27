import {
  BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload,
  BuyOutSurplusFacilityPerformanceDetail,
  BuyOutSurplusFacilityTransactionSummary,
} from 'cca-api';

import { toHistoricalFacilities } from './historical-facilities';

describe('CCA3 historical facility API mapping', () => {
  const facility: BuyOutSurplusFacilityPerformanceDetail = {
    facilityBusinessId: 'AIC-F00064',
    siteName: 'Recorded site name',
    targetPeriodType: 'TP8',
    performanceDataId: 64,
    performanceDataReportVersion: 3,
    submissionType: 'PRIMARY',
    transactionCode: 'CCA080012',
    data: {
      performanceOutcome: 'TARGET_NOT_MET',
      buyOutRequired: '9007199254740993.12',
      surplusGained: '0',
      surplusUsed: '0.0000000',
      previousPaidFees: '825.00',
      previousSurplusUsed: '25',
      previousSurplusGained: '0',
      chargeType: 'FEE',
      invoicedBuyOutFee: '275.00',
      schemeTotalSurplus: '50',
    },
  };
  const summary: BuyOutSurplusFacilityTransactionSummary = {
    targetPeriodType: 'TP8',
    chargeType: 'FEE',
    targetPeriodBuyOutCost: '11',
    totalFacilities: 1,
    invoicedBuyOutFee: '275.00',
  };

  const payload = (
    summaries: BuyOutSurplusFacilityTransactionSummary[] = [summary],
  ): BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload => ({
    runId: 'BOS-TP8007',
    facilities: [facility],
    summary: summaries,
  });

  it.each([true, false])('preserves the primary rules flag (%s) and saved facility data', (primaryRulesApplied) => {
    const response = payload();
    response.facilities = [{ ...facility, primaryRulesApplied }];
    const before = structuredClone(response);
    const [result] = toHistoricalFacilities(response);

    expect(result).toEqual({
      ...facility.data,
      facilityBusinessId: 'AIC-F00064',
      siteName: 'Recorded site name',
      targetPeriod: 'TP8',
      version: 3,
      submissionType: 'PRIMARY',
      primaryRulesApplied,
      transactionCode: 'CCA080012',
      buyOutCost: '11',
    });
    expect(response).toEqual(before);
    expect(result).not.toHaveProperty('calculationType');
  });

  it.each(['FEE', 'REFUND'] as const)('uses the same-period recorded cost from a %s summary', (chargeType) => {
    const summaries = [
      { ...summary, targetPeriodType: 'TP7' as const, targetPeriodBuyOutCost: '999' },
      { ...summary, chargeType },
    ];
    expect(toHistoricalFacilities(payload(summaries))[0].buyOutCost).toBe('11');
  });

  it('accepts a shared cost for fee and refund summaries and preserves a supplied zero cost', () => {
    expect(toHistoricalFacilities(payload([summary, { ...summary, chargeType: 'REFUND' }]))[0].buyOutCost).toBe('11');
    expect(toHistoricalFacilities(payload([{ ...summary, targetPeriodBuyOutCost: '0' }]))[0].buyOutCost).toBe('0');
  });

  it('reads a numeric cost from an action stored before the decimal string contract', () => {
    const legacySummary = { ...summary, targetPeriodBuyOutCost: 11 as unknown as string };
    expect(toHistoricalFacilities(payload([legacySummary]))[0].buyOutCost).toBe('11');
  });

  it('treats equal decimal costs as one cost and keeps the API decimal string', () => {
    const summaries = [
      { ...summary, targetPeriodBuyOutCost: '11.50' },
      { ...summary, chargeType: 'REFUND' as const, targetPeriodBuyOutCost: '11.5' },
    ];
    expect(toHistoricalFacilities(payload(summaries))[0].buyOutCost).toBe('11.50');
  });

  it('does not merge different historical costs that differ by a penny at large values', () => {
    const summaries = [
      { ...summary, targetPeriodBuyOutCost: '100000000000000.01' },
      { ...summary, chargeType: 'REFUND' as const, targetPeriodBuyOutCost: '100000000000000.02' },
    ];
    expect(toHistoricalFacilities(payload(summaries))[0].buyOutCost).toBeUndefined();
  });

  it.each([
    { summaries: [] },
    { summaries: [{ ...summary, targetPeriodType: 'TP7' as const }] },
    { summaries: [summary, { ...summary, chargeType: 'REFUND' as const, targetPeriodBuyOutCost: '12' }] },
    { summaries: [{ ...summary, targetPeriodBuyOutCost: undefined }] },
    { summaries: [{ ...summary, targetPeriodBuyOutCost: null }] },
    { summaries: [{ ...summary, targetPeriodBuyOutCost: '' }] },
    { summaries: [{ ...summary, targetPeriodBuyOutCost: 'abc' }] },
    { summaries: [{ ...summary, targetPeriodBuyOutCost: '-1' }] },
  ])('leaves an unavailable or conflicting historical cost blank: %j', ({ summaries }) => {
    // The facility's own result remains available, even when no invoice was issued.
    expect(toHistoricalFacilities(payload(summaries))[0]).toMatchObject({
      buyOutCost: undefined,
      buyOutRequired: '9007199254740993.12',
      invoicedBuyOutFee: '275.00',
    });
  });

  it('returns no facilities before the action is loaded', () => {
    expect(toHistoricalFacilities(undefined)).toEqual([]);
  });
});
