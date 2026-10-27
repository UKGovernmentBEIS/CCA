import { BuyOutSurplusFacilityAccountProcessingRequestMetadata, RequestDetailsDTO } from 'cca-api';

import { expandBuyoutHistoryTypes, toHistoryMetadata } from './workflow-history-tab.utils';

describe('CCA3 history metadata (H1–H4)', () => {
  const workflow = (metadata?: BuyOutSurplusFacilityAccountProcessingRequestMetadata): RequestDetailsDTO => ({
    requestType: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING',
    requestMetadata: metadata,
  });

  it('H1 preserves every buy-out ID in TP order and omits account TPR metadata', () => {
    expect(toHistoryMetadata(workflow({ buyOutTransactionCodes: { TP8: 'CCA080007', TP7: 'CCA070021' } }))).toEqual({
      cca3: { buyOutTransactionIds: ['CCA070021', 'CCA080007'], refundTransactionIds: [] },
    });
  });

  it('H2 keeps mixed buy-outs and refunds separate without stringifying their maps', () => {
    expect(
      toHistoryMetadata(
        workflow({
          buyOutTransactionCodes: { TP8: 'CCA080003', TP7: 'CCA070023' },
          refundTransactionCodes: { TP8: 'CCA080024' },
        }),
      ),
    ).toEqual({
      cca3: { buyOutTransactionIds: ['CCA070023', 'CCA080003'], refundTransactionIds: ['CCA080024'] },
    });
  });

  it('H2 supports refund-only metadata', () => {
    expect(toHistoryMetadata(workflow({ refundTransactionCodes: { TP7: 'CCA070024' } }))).toEqual({
      cca3: { buyOutTransactionIds: [], refundTransactionIds: ['CCA070024'] },
    });
  });

  it.each([undefined, {}, { buyOutTransactionCodes: {}, refundTransactionCodes: {} }])(
    'H3 retains a no-transaction result for metadata %j',
    (metadata) => {
      expect(toHistoryMetadata(workflow(metadata))).toEqual({
        cca3: { buyOutTransactionIds: [], refundTransactionIds: [] },
      });
    },
  );

  it('H4 retains CCA2 version, result and legacy transaction ID', () => {
    const metadata = {
      type: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
      targetPeriodType: 'TP6',
      performanceDataReportVersion: 2,
      tpOutcome: 'TARGET_MET',
      transactionId: 'CCA060021',
    };
    expect(toHistoryMetadata({ requestType: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING', requestMetadata: metadata })).toEqual(
      {
        cca2: metadata,
      },
    );
  });

  it('H4 also accepts the generated CCA2 transactionCode field', () => {
    const metadata = { type: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING', transactionCode: 'CCA060022' };
    expect(
      toHistoryMetadata({ requestType: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING', requestMetadata: metadata }).cca2,
    ).toMatchObject({ transactionId: 'CCA060022' });
  });

  it('preserves variation initiator headings', () => {
    const metadata = { type: 'UNDERLYING_AGREEMENT_VARIATION', initiatorRoleType: 'REGULATOR' };
    expect(toHistoryMetadata({ requestType: 'UNDERLYING_AGREEMENT_VARIATION', requestMetadata: metadata })).toEqual({
      initiatorRoleType: 'REGULATOR',
    });
  });

  it('H4 expands only the existing checkbox, preserving other types and eliminating duplicates', () => {
    const selected = [
      'UNDERLYING_AGREEMENT',
      'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
      'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING',
      'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
    ];
    expect(expandBuyoutHistoryTypes(selected)).toEqual([
      'UNDERLYING_AGREEMENT',
      'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
      'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING',
    ]);
    expect(selected).toHaveLength(4);
    expect(expandBuyoutHistoryTypes([])).toEqual([]);
    expect(expandBuyoutHistoryTypes(['ADMIN_TERMINATION'])).toEqual(['ADMIN_TERMINATION']);
  });
});
