import { HistoricalFacility, toHistoricalFacilities } from '../historical-facilities';
import { facilityDetailsFixtures } from '../testing/facility-details-fixtures';
import { mockAccountAction126, mockFacility } from '../testing/mock-data';
import { toFacilityCalculationPresentation } from './facility-calculation-presentation';

describe('facility layouts from the historical API response', () => {
  const facility = (overrides: Partial<HistoricalFacility> = {}) =>
    mockFacility({
      surplusUsed: '0',
      previousSurplusUsed: '0',
      previousSurplusGained: '0',
      previousPaidFees: '0',
      ...overrides,
    });

  it.each(facilityDetailsFixtures)(
    'selects the financial layout and totals in mock $node',
    ({ details, calculation }) => {
      expect(toFacilityCalculationPresentation(details.facility)).toEqual(calculation);
    },
  );

  it.each([
    ['PRIMARY', true, 'primary-surplus', 'primary-buyout'],
    ['SECONDARY', true, 'primary-surplus', 'primary-buyout'],
    ['PRIMARY', false, 'secondary-surplus', 'secondary-buyout-after-buyout'],
    ['SECONDARY', false, 'secondary-surplus', 'secondary-buyout-after-buyout'],
    ['PRIMARY', undefined, 'secondary-surplus', 'secondary-buyout-after-buyout'],
    ['SECONDARY', undefined, 'secondary-surplus', 'secondary-buyout-after-buyout'],
  ] as const)(
    'uses the recorded rules for a %s report with primaryRulesApplied=%s and zero/absent previous amounts',
    (submissionType, primaryRulesApplied, surplusScenario, buyoutScenario) => {
      for (const zero of ['0', '0.0000000', undefined]) {
        for (const performanceOutcome of ['TARGET_MET', 'TARGET_NOT_MET'] as const) {
          const record = facility({
            submissionType,
            primaryRulesApplied,
            performanceOutcome,
            chargeType: performanceOutcome === 'TARGET_MET' ? undefined : 'FEE',
            previousPaidFees: zero,
            previousSurplusGained: zero,
            previousSurplusUsed: zero,
          });
          expect(toFacilityCalculationPresentation(record)?.scenario).toBe(
            performanceOutcome === 'TARGET_MET' ? surplusScenario : buyoutScenario,
          );
        }
      }
    },
  );

  it.each(['PRIMARY', 'SECONDARY'] as const)(
    'uses refund rows for a %s report for either final result',
    (submissionType) => {
      for (const performanceOutcome of ['TARGET_MET', 'TARGET_NOT_MET'] as const) {
        const result = toFacilityCalculationPresentation(
          facility({ submissionType, performanceOutcome, chargeType: 'REFUND', previousPaidFees: '1000' }),
        );
        expect(result.scenario).toBe(
          performanceOutcome === 'TARGET_MET' ? 'secondary-target-met-refund' : 'secondary-target-not-met-refund',
        );
      }
    },
  );

  it.each([{ previousPaidFees: '825' }, { previousSurplusUsed: '25' }, { primaryRulesApplied: false }])(
    'shows a payment breakdown with %j',
    (values) => {
      expect(toFacilityCalculationPresentation(facility(values))?.scenario).toBe('secondary-buyout-after-buyout');
    },
  );

  it('shows rebanked surplus when Target met has no refund', () => {
    expect(
      toFacilityCalculationPresentation(
        facility({ performanceOutcome: 'TARGET_MET', previousSurplusUsed: '40', chargeType: undefined }),
      ),
    ).toEqual({ scenario: 'secondary-surplus' });
  });

  it('keeps the surplus-used breakdown for a Secondary report when the saved result used surplus', () => {
    expect(toFacilityCalculationPresentation(facility({ submissionType: 'SECONDARY', surplusUsed: '12' }))).toEqual({
      scenario: 'primary-buyout',
      showTransaction: true,
    });
  });

  it('reconstructs the action 126 gross fee without changing the recorded payments or final fee', () => {
    const record = toHistoricalFacilities(mockAccountAction126.payload)[0];
    const original = { ...record };
    expect(toFacilityCalculationPresentation(record)).toEqual({
      scenario: 'secondary-buyout-after-buyout',
      grossBuyoutFee: '1100',
    });
    expect(record).toEqual(original);
  });

  it.each([
    ['FEE', '520', '1000', '1520'],
    ['REFUND', '1800', '320', '1480'],
    [undefined, '1000', '0.0000000', '1000'],
    [undefined, '0', '0', '0'],
    ['FEE', '0', '1480', '1480'],
    ['FEE', '0.1', '0.2', '0.3'],
    ['REFUND', '0.3', '0.2', '0.1'],
    ['FEE', '9007199254740993.1234567', '0.0000001', '9007199254740993.1234568'],
    ['REFUND', '9007199254740993.1234567', '0.0000001', '9007199254740993.1234566'],
    ['FEE', `0.${'0'.repeat(330)}1`, `0.${'0'.repeat(330)}2`, `0.${'0'.repeat(330)}3`],
  ] as const)(
    'derives gross for %s, paid %s and settlement %s',
    (chargeType, previousPaidFees, invoicedBuyOutFee, gross) => {
      expect(
        toFacilityCalculationPresentation(
          facility({ primaryRulesApplied: false, chargeType, previousPaidFees, invoicedBuyOutFee }),
        ),
      ).toMatchObject({ grossBuyoutFee: gross });
    },
  );

  it.each([
    { previousPaidFees: undefined },
    { previousPaidFees: null },
    { previousPaidFees: 'NaN' },
    { previousPaidFees: 'Infinity' },
    { previousPaidFees: '-1' },
    { invoicedBuyOutFee: undefined },
    { invoicedBuyOutFee: 'invalid' },
    { invoicedBuyOutFee: '-1' },
    { chargeType: 'REFUND' as const, previousPaidFees: '20', invoicedBuyOutFee: '21' },
    { chargeType: undefined, invoicedBuyOutFee: '20' },
  ])('leaves an unavailable or inconsistent gross amount blank without hiding the page: %j', (values) => {
    expect(toFacilityCalculationPresentation(facility({ primaryRulesApplied: false, ...values }))).toMatchObject({
      grossBuyoutFee: null,
    });
  });

  it.each(['0', '0.0000000'])('shows a fully-offset fee %s without a transaction', (invoicedBuyOutFee) => {
    expect(
      toFacilityCalculationPresentation(facility({ surplusUsed: '40', invoicedBuyOutFee, chargeType: undefined })),
    ).toEqual({ scenario: 'primary-buyout', showTransaction: false });
  });

  it.each(['SUBMITTED', 'OUTSTANDING'] as const)('does not expose a non-final %s result', (performanceOutcome) => {
    expect(toFacilityCalculationPresentation(facility({ performanceOutcome }))).toBeUndefined();
  });
});
