import { toFacilitiesListRow } from './facilities-list-row';
import { mockFacility } from './testing/mock-data';

describe('CCA3 facility table presentation', () => {
  it('shows the supplied settlement for a buy-out transaction, without recalculating it', () => {
    expect(
      toFacilitiesListRow(mockFacility({ buyOutRequired: '900', buyOutCost: '37', invoicedBuyOutFee: '1001.1234567' })),
    ).toEqual({
      siteName: 'West London Site',
      facilityBusinessId: 'AIC-F00064',
      targetPeriod: '7',
      targetPeriodId: 'TP7',
      result: 'Target not met',
      surplusUsed: '12',
      buyoutAmount: '1,001.12',
      refundAmount: '',
      surplusGained: '',
      detailsAvailable: true,
    });
  });

  it('shows refund and target-met zero surplus; previous surplus is not current surplus use', () => {
    expect(
      toFacilitiesListRow(
        mockFacility({
          performanceOutcome: 'TARGET_MET',
          chargeType: 'REFUND',
          surplusGained: '0.0000000',
          previousSurplusUsed: '20',
          previousSurplusGained: '50',
          invoicedBuyOutFee: '202.0000',
        }),
      ),
    ).toMatchObject({ surplusUsed: '', buyoutAmount: '', refundAmount: '202.00', surplusGained: '0.0000000' });
  });

  it.each(['0', '0.0000000', '202'])('leaves transaction-only cells blank without a transaction ID (%s)', (fee) => {
    expect(toFacilitiesListRow(mockFacility({ transactionCode: undefined, invoicedBuyOutFee: fee }))).toMatchObject({
      buyoutAmount: '',
      refundAmount: '',
    });
  });

  it.each(['101', '22'])('shows a target-not-met refund only in the overpayment column (%s)', (refund) => {
    expect(
      toFacilitiesListRow(
        mockFacility({
          chargeType: 'REFUND',
          invoicedBuyOutFee: refund,
          surplusUsed: undefined,
          surplusGained: '0',
        }),
      ),
    ).toMatchObject({ surplusUsed: '', buyoutAmount: '', refundAmount: `${refund}.00`, surplusGained: '' });
  });

  it('retains an explicitly supplied transaction zero and leaves missing values blank', () => {
    expect(toFacilitiesListRow(mockFacility({ invoicedBuyOutFee: '0.0000000' })).buyoutAmount).toBe('0.00');
    expect(toFacilitiesListRow(mockFacility({ invoicedBuyOutFee: undefined, surplusUsed: undefined }))).toMatchObject({
      buyoutAmount: '',
      surplusUsed: '',
    });
    expect(
      toFacilitiesListRow(mockFacility({ performanceOutcome: 'TARGET_MET', surplusGained: undefined })).surplusGained,
    ).toBe('');
  });

  it.each(['0', '0.0000000'])('preserves explicitly supplied surplus-used zero (%s)', (surplusUsed) => {
    expect(toFacilitiesListRow(mockFacility({ surplusUsed })).surplusUsed).toBe(surplusUsed);
  });

  it.each([undefined, null, '-1'])('leaves absent or negative surplus use blank (%s)', (surplusUsed) => {
    expect(toFacilitiesListRow(mockFacility({ surplusUsed })).surplusUsed).toBe('');
  });

  it('retains a positive surplus too small for floating-point arithmetic', () => {
    const surplusUsed = `0.${'0'.repeat(330)}1`;
    expect(toFacilitiesListRow(mockFacility({ surplusUsed })).surplusUsed).toBe(surplusUsed);
  });

  it('does not use submission type to decide which financial values to show', () => {
    expect(toFacilitiesListRow(mockFacility({ submissionType: 'SECONDARY' }))).toEqual(
      toFacilitiesListRow(mockFacility()),
    );
  });
});
