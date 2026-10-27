import {
  BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload,
  BuyOutSurplusFacilityTransactionSummary,
} from 'cca-api';

import { toBuyoutSurplusAccountSummaryData } from './buyout-surplus-account-summary-data';
import { mockAccountAction126, mockAccountPayload, mockBuyout, mockFacility, mockRefund } from './testing/mock-data';

describe('CCA3 historical account summaries', () => {
  it('groups cascading outcomes oldest first, keeping two buy-outs and one refund separate', () => {
    const payload = mockAccountPayload();
    const before = structuredClone(payload);
    const sections = toBuyoutSurplusAccountSummaryData(payload);
    expect(sections.map(({ header }) => header)).toEqual([
      'Details',
      'TP7 Buy-out',
      'TP8 Buy-out',
      'TP8 Refund',
      'Recipients',
    ]);
    expect(sections[0].data).toEqual([{ key: 'Run ID', value: ['BOS-TP8004'] }]);
    expect(sections[1].data).toEqual([
      { key: 'Cost (GBP / tCO₂e)', value: ['37.00'] },
      {
        key: 'Buy-out MoA file',
        isFileList: true,
        value: [{ fileName: 'CCA070023.pdf', downloadUrl: './file-download/document/tp7-buyout-notice' }],
      },
      { key: 'Total facilities', value: ['50'] },
      { key: 'Total buy-out fee (GBP)', value: ['19,900.00'] },
      { key: 'Due date', value: ['1 Jul 2028'] },
    ]);
    expect(sections[3].data).toEqual([
      {
        key: 'Refund letter',
        isFileList: true,
        value: [{ fileName: 'CCA080024.pdf', downloadUrl: './file-download/document/tp8-refund-notice' }],
      },
      { key: 'Total facilities', value: ['3'] },
      { key: 'Total refund (GBP)', value: ['199.25'] },
    ]);
    expect(payload).toEqual(before);
  });

  it('keeps two-plus-two transactions and documents separate', () => {
    const sections = toBuyoutSurplusAccountSummaryData(
      mockAccountPayload({
        summary: [
          mockRefund(),
          mockBuyout({ targetPeriodType: 'TP8' }),
          mockRefund({ targetPeriodType: 'TP7', officialNotice: { name: 'CCA070024.pdf', uuid: 'tp7-refund-notice' } }),
          mockBuyout(),
        ],
      }),
    );
    expect(sections.map(({ header }) => header)).toEqual([
      'Details',
      'TP7 Buy-out',
      'TP7 Refund',
      'TP8 Buy-out',
      'TP8 Refund',
      'Recipients',
    ]);
    expect(sections[2].data[0]).toMatchObject({
      value: [{ fileName: 'CCA070024.pdf', downloadUrl: './file-download/document/tp7-refund-notice' }],
    });
  });

  it.each([
    { summary: [mockBuyout()], headers: ['Details', 'TP7 Buy-out', 'Recipients'] },
    { summary: [mockRefund()], headers: ['Details', 'TP8 Refund', 'Recipients'] },
    {
      summary: [mockRefund({ targetPeriodType: 'TP7' }), mockBuyout()],
      headers: ['Details', 'TP7 Buy-out', 'TP7 Refund', 'Recipients'],
    },
  ])('supports a single-period outcome: $headers', ({ summary, headers }) => {
    expect(toBuyoutSurplusAccountSummaryData(mockAccountPayload({ summary })).map(({ header }) => header)).toEqual(
      headers,
    );
  });

  it('omits financial sections and recipients when there are no summary entries or facility transactions', () => {
    const summaries: BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload['summary'][] = [undefined, []];
    for (const summary of summaries) {
      expect(
        toBuyoutSurplusAccountSummaryData(
          mockAccountPayload({
            summary,
            facilities: [mockFacility({ transactionCode: undefined, chargeType: undefined })],
          }),
        ).map(({ header }) => header),
      ).toEqual(['Details']);
    }
  });

  it('preserves the TP8 financial summary when optional notice metadata and recipients are omitted', () => {
    const payload: BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload = {
      ...mockAccountAction126.payload,
      defaultContacts: undefined,
      summary: mockAccountAction126.payload.summary.map((summary): BuyOutSurplusFacilityTransactionSummary => ({
        ...summary,
        officialNotice: undefined,
      })),
    };
    expect(
      toBuyoutSurplusAccountSummaryData(payload).map(({ header, data }) => ({
        header,
        data,
      })),
    ).toEqual([
      { header: 'Details', data: [{ key: 'Run ID', value: ['BOS-TP8007'] }] },
      {
        header: 'TP8 Buy-out',
        data: [
          { key: 'Cost (GBP / tCO₂e)', value: ['11.00'] },
          { key: 'Buy-out MoA file', isFileList: true, value: [] },
          { key: 'Total facilities', value: ['1'] },
          { key: 'Total buy-out fee (GBP)', value: ['275.00'] },
          { key: 'Due date', value: ['27 Oct 2026'] },
        ],
      },
    ]);
  });

  it('groups mixed TP summaries independently of which notices have metadata', () => {
    const payload = mockAccountPayload({
      summary: [
        mockRefund({ officialNotice: undefined }),
        mockBuyout({ targetPeriodType: 'TP8' }),
        mockRefund({ targetPeriodType: 'TP7' }),
        mockBuyout({ officialNotice: undefined }),
      ],
    });
    const before = structuredClone(payload);
    const sections = toBuyoutSurplusAccountSummaryData(payload);
    expect(sections.map(({ header }) => header)).toEqual([
      'Details',
      'TP7 Buy-out',
      'TP7 Refund',
      'TP8 Buy-out',
      'TP8 Refund',
      'Recipients',
    ]);
    expect(sections[1].data[1]).toEqual({ key: 'Buy-out MoA file', isFileList: true, value: [] });
    expect(sections[1].data[3]).toEqual({ key: 'Total buy-out fee (GBP)', value: ['19,900.00'] });
    expect(sections[4].data).toEqual([
      { key: 'Refund letter', isFileList: true, value: [] },
      { key: 'Total facilities', value: ['3'] },
      { key: 'Total refund (GBP)', value: ['199.25'] },
    ]);
    expect(payload).toEqual(before);
  });

  it('preserves supplied recipients when a transaction exists but its notice is missing', () => {
    expect(toBuyoutSurplusAccountSummaryData(mockAccountPayload({ summary: [] })).map(({ header }) => header)).toEqual([
      'Details',
      'Recipients',
    ]);
  });

  it('shows the supplied common historical recipient list once and in its original order', () => {
    expect(toBuyoutSurplusAccountSummaryData(mockAccountPayload()).at(-1).data).toEqual([
      {
        key: 'Users',
        value: [
          'John William, Responsible person, williamsj@example.com',
          'Matthew Johnson, Administrative contact, mjohnson@example.com',
        ],
      },
    ]);
    expect(
      toBuyoutSurplusAccountSummaryData(mockAccountPayload({ defaultContacts: [] })).map(({ header }) => header),
    ).not.toContain('Recipients');
  });

  it('never turns missing financial values into zero or creates a broken document link', () => {
    const sections = toBuyoutSurplusAccountSummaryData(
      mockAccountPayload({
        summary: [
          mockBuyout({ targetPeriodBuyOutCost: undefined, officialNotice: {}, invoicedBuyOutFee: '0.0000000' }),
        ],
      }),
    );
    expect(sections[1].data[0]).toMatchObject({ value: [''] });
    expect(sections[1].data[1]).toMatchObject({ isFileList: true, value: [] });
    expect(sections[1].data[3]).toMatchObject({ value: ['0.00'] });
    expect(toBuyoutSurplusAccountSummaryData(undefined)).toEqual([]);
  });
});
