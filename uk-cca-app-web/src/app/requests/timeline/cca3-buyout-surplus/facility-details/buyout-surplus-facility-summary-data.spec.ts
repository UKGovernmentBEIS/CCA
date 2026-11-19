import { toHistoricalFacilities } from '../historical-facilities';
import { facilityDetailsFixtures, mockHistoricalFacility } from '../testing/facility-details-fixtures';
import { mockAccountAction126 } from '../testing/mock-data';
import { toBuyoutSurplusFacilitySummaryData } from './buyout-surplus-facility-summary-data';
import { FacilityCalculationPresentation } from './facility-calculation.types';
import { toFacilityCalculationPresentation } from './facility-calculation-presentation';

describe('CCA3 historical facility summaries', () => {
  it.each(['0', '0.0000000'])('retains every required Primary buy-out row when its supplied value is %s', (zero) => {
    const details = mockHistoricalFacility({
      buyOutRequired: zero,
      surplusUsed: zero,
      buyOutCost: zero,
      invoicedBuyOutFee: zero,
      transactionCode: undefined,
    });
    const rows = toBuyoutSurplusFacilitySummaryData(details, { scenario: 'primary-buyout', showTransaction: true })[1]
      .data;
    expect(rows.map(({ key }) => key)).toEqual([
      'Amount above target (tCO₂e)',
      'Surplus used (tCO₂e)',
      'Cost (GBP / tCO₂e)',
      'Total buy-out fee (GBP)',
    ]);
    expect(rows.map(({ value }) => value[0])).toEqual([zero, zero, '0.00', '0.00']);
  });

  it.each([
    { submissionType: 'SECONDARY', primaryRulesApplied: true, type: 'Primary' },
    { submissionType: 'PRIMARY', primaryRulesApplied: false, type: 'Secondary' },
    { submissionType: 'PRIMARY', primaryRulesApplied: undefined, type: 'Secondary' },
  ] as const)(
    'shows $type for primaryRulesApplied=$primaryRulesApplied regardless of $submissionType submission',
    ({ submissionType, primaryRulesApplied, type }) => {
      const { details, calculation } = facilityDetailsFixtures[5];
      const flaggedDetails = { ...details, facility: { ...details.facility, submissionType, primaryRulesApplied } };
      expect(
        toBuyoutSurplusFacilitySummaryData(flaggedDetails, calculation)[0].data.map(({ value }) => value[0]),
      ).toEqual(['BOS-TP8004', 'AIC-F00064', 'West London Site', 'TP8-V3 (Target not met)', type]);
    },
  );

  it.each(['0', '0.0000000'])(
    'retains exactly-paid Secondary fees (%s) without requiring or showing a transaction',
    (zero) => {
      const details = mockHistoricalFacility({ previousPaidFees: '1000', invoicedBuyOutFee: zero, chargeType: 'FEE' });
      const summary = toBuyoutSurplusFacilitySummaryData(details, {
        scenario: 'secondary-buyout-after-buyout',
        grossBuyoutFee: '1000',
      })[1];
      expect(summary.data.find(({ key }) => key === 'Secondary buy-out fee (GBP)').value).toEqual(['0.00']);
      expect(summary.data.some(({ key }) => key === 'Transaction ID')).toBe(false);
    },
  );

  it.each(['secondary-surplus', 'secondary-target-met-refund'] as const)(
    'keeps current and previous supplied surplus zeros for %s',
    (scenario) => {
      const details = mockHistoricalFacility({
        performanceOutcome: 'TARGET_MET',
        surplusGained: '0.0000000',
        previousSurplusGained: '0',
        previousSurplusUsed: '0',
      });
      const rows = toBuyoutSurplusFacilitySummaryData(details, { scenario })[1].data;
      expect(rows[0].value).toEqual(['0.0000000']);
      expect(rows.find(({ key }) => key.startsWith('Previous surplus')).value).toEqual(['0']);
    },
  );

  it('adds previous payments to the final fee in action 126', () => {
    const facility = toHistoricalFacilities(mockAccountAction126.payload)[0];
    const details = { ...mockHistoricalFacility(), facility };
    const rows = toBuyoutSurplusFacilitySummaryData(details, toFacilityCalculationPresentation(facility))[1].data;
    expect(rows.find(({ key }) => key === 'Total buy-out fee (GBP)').value).toEqual(['1,100.00']);
    expect(rows.find(({ key }) => key === 'Previous buy-out fee received (GBP)').value).toEqual(['825.00']);
    expect(rows.find(({ key }) => key === 'Secondary buy-out fee (GBP)').value).toEqual(['275.00']);
  });

  it('formats large monetary values to two decimals while preserving emissions precision', () => {
    const details = mockHistoricalFacility({ buyOutRequired: '9007199254740993.1234567', buyOutCost: '37.0000000' });
    const rows = toBuyoutSurplusFacilitySummaryData(details, {
      scenario: 'secondary-buyout-after-surplus',
      grossBuyoutFee: '9007199254740994.0000001',
    })[1].data;
    expect(rows.slice(0, 3).map(({ value }) => value[0])).toEqual([
      '9,007,199,254,740,993.1234567',
      '37.00',
      '9,007,199,254,740,994.00',
    ]);
  });

  it('matches the primary surplus mock without a Date banked row', () => {
    const details = mockHistoricalFacility({ surplusGained: undefined });
    const rows = toBuyoutSurplusFacilitySummaryData(details, { scenario: 'primary-surplus' })[1].data;
    expect(rows.map(({ value }) => value[0] || '')).toEqual(['']);
    expect(rows.map(({ key }) => key)).toEqual(['Surplus gained (tCO₂e)']);
  });

  it('shows a transaction reference only when the selected layout requests it', () => {
    const details = mockHistoricalFacility({ invoicedBuyOutFee: '0', surplusUsed: '40' });
    const withReference = toBuyoutSurplusFacilitySummaryData(details, {
      scenario: 'primary-buyout',
      showTransaction: true,
    });
    const withoutReference = toBuyoutSurplusFacilitySummaryData(details, {
      scenario: 'primary-buyout',
      showTransaction: false,
    });
    expect(withReference[1].data.at(-1).key).toBe('Transaction ID');
    expect(withoutReference[1].data.at(-1)).toMatchObject({ key: 'Total buy-out fee (GBP)', value: ['0.00'] });
  });

  it('keeps prior payments and the net fee visible when previous surplus is removed', () => {
    const details = mockHistoricalFacility({
      submissionType: 'SECONDARY',
      previousSurplusGained: '50',
      previousPaidFees: '100',
      invoicedBuyOutFee: '200',
      chargeType: 'FEE',
    });
    const summary = toBuyoutSurplusFacilitySummaryData(details, toFacilityCalculationPresentation(details.facility));
    expect(summary[1].data.map(({ key, value }) => [key, value[0]])).toEqual([
      ['Amount above target (tCO₂e)', '40'],
      ['Cost (GBP / tCO₂e)', '37.00'],
      ['Total buy-out fee (GBP)', '300.00'],
      ['Previous surplus gained/removed (tCO₂e)', '50'],
      ['Previous buy-out fee received (GBP)', '100.00'],
      ['Secondary buy-out fee (GBP)', '200.00'],
      ['Transaction ID', 'CCA070021'],
    ]);
  });

  it('shows an exactly-paid after-surplus correction without a new transaction', () => {
    const details = mockHistoricalFacility({
      submissionType: 'SECONDARY',
      previousSurplusGained: '50',
      previousPaidFees: '520',
      invoicedBuyOutFee: '0.0000',
      chargeType: undefined,
      transactionCode: undefined,
    });
    const rows = toBuyoutSurplusFacilitySummaryData(details, toFacilityCalculationPresentation(details.facility))[1]
      .data;
    expect(rows.find(({ key }) => key === 'Total buy-out fee (GBP)').value).toEqual(['520.00']);
    expect(rows.find(({ key }) => key === 'Previous buy-out fee received (GBP)').value).toEqual(['520.00']);
    expect(rows.find(({ key }) => key === 'Secondary buy-out fee (GBP)').value).toEqual(['0.00']);
    expect(rows.some(({ key }) => key === 'Transaction ID')).toBe(false);
  });

  it.each([undefined, '', ' '])('does not invent missing transaction references (%s)', (transactionCode) => {
    const details = mockHistoricalFacility({ transactionCode });
    const calculation: FacilityCalculationPresentation = { scenario: 'secondary-target-met-refund' };
    expect(
      toBuyoutSurplusFacilitySummaryData(details, calculation)[1].data.some(({ key }) => key === 'Transaction ID'),
    ).toBe(false);
  });
});
