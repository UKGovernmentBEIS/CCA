import { SummaryData, SummarySection } from '@shared/components';

import {
  mockFacilityTransactionFee,
  mockFacilityTransactionPaid,
  mockFacilityTransactionRefund,
  mockFacilityTransactionTerminated,
} from '../../testing/cca3-facility-mock-data';
import { toTransactionDetails } from './cca3-transaction-details-data';

const findRow = (data: SummaryData, key: string): SummarySection | undefined =>
  data.flatMap((section) => section.data).find((row) => row.key === key);

const rowValue = (data: SummaryData, key: string): string => {
  const value = findRow(data, key)?.value;
  // Null values render as empty cells in the summary, so treat them as empty strings here too.
  return Array.isArray(value) ? String(value[0] ?? '') : String(value ?? '');
};

const sectionHeaders = (data: SummaryData): string[] => data.map((section) => section.header);

describe('toTransactionDetails', () => {
  it('compares decimal reconciliation amounts without losing pennies at large values', () => {
    const data = toTransactionDetails(
      {
        ...mockFacilityTransactionFee,
        expectedAmount: '100000000000000.01',
        receivedAmount: '100000000000000.02',
        amountAllocated: '100000000000000.01',
      },
      true,
    );
    expect(rowValue(data, 'Received amount (GBP)')).toBe(
      '100,000,000,000,000.02 (This amount is higher than the expected amount)',
    );
    expect(rowValue(data, 'Amount allocated to paid facilities (GBP)')).toBe(
      '100,000,000,000,000.01 (This amount is lower than the received amount)',
    );
  });

  it.each(['FEE', 'REFUND'] as const)('formats every %s monetary summary with exactly two decimals', (chargeType) => {
    const data = toTransactionDetails(
      {
        ...mockFacilityTransactionFee,
        chargeType,
        invoicedBuyOutFee: '999999999999999999.99',
        expectedAmount: '120.5',
        receivedAmount: '120.5',
        amountAllocated: '0',
      },
      true,
    );
    expect(
      data
        .flatMap((section) => section.data)
        .filter((row) => row.key.includes('(GBP)'))
        .map((row) => row.value),
    ).toEqual(
      chargeType === 'FEE'
        ? [
            ['999,999,999,999,999,999.99'],
            ['120.50'],
            ['120.50'],
            ['0.00 (This amount is lower than the received amount)'],
          ]
        : [['999,999,999,999,999,999.99'], ['120.50'], ['0.00']],
    );
  });

  describe('buy-out transaction', () => {
    it('should show the buy-out details with the reconciliation section', () => {
      const data = toTransactionDetails(mockFacilityTransactionFee, true);

      expect(sectionHeaders(data)).toEqual(['Details', 'Buy-out fee reconciliation']);
      expect(findRow(data, 'Buy-out MoA file')).toBeDefined();
      expect(findRow(data, 'Refund letter')).toBeUndefined();
      expect(rowValue(data, 'Total buy-out amount (GBP)')).toBe('1,250.00');
      expect(rowValue(data, 'Due date')).toBe('30 Jun 2026');
    });

    it('should link the MoA file to the transaction file download route', () => {
      const data = toTransactionDetails(mockFacilityTransactionFee, true);

      expect(findRow(data, 'Buy-out MoA file')?.value).toEqual([
        {
          fileName: 'CCA080001 buy-out and surplus summary report.pdf',
          downloadUrl: 'file-download/3f1c1e6c-9f2b-4c81-9d78-6b2a4e0d1a11',
        },
      ]);
    });

    it('should remark the received amount when it differs from the expected amount', () => {
      const data = toTransactionDetails(mockFacilityTransactionFee, true);

      expect(rowValue(data, 'Received amount (GBP)')).toBe('0.00 (This amount is lower than the expected amount)');
      expect(rowValue(data, 'Amount allocated to paid facilities (GBP)')).toBe('0.00');
    });

    it('should remark the allocated amount when it differs from the received amount', () => {
      const data = toTransactionDetails(mockFacilityTransactionPaid, true);

      expect(rowValue(data, 'Received amount (GBP)')).toBe('500.00 (This amount is higher than the expected amount)');
      expect(rowValue(data, 'Amount allocated to paid facilities (GBP)')).toBe(
        '430.50 (This amount is lower than the received amount)',
      );
    });

    it('should offer the mark all as paid action while facilities are pending', () => {
      const data = toTransactionDetails(mockFacilityTransactionFee, true);
      const row = findRow(data, 'Facilities marked as paid');

      expect(rowValue(data, 'Facilities marked as paid')).toBe('10 out of 12');
      expect(row?.action).toBe('Mark all as paid');
      expect(row?.actionLink).toBe('./mark-facilities/all-paid');
    });

    it('should hide the mark all as paid action when every facility is marked', () => {
      const data = toTransactionDetails(mockFacilityTransactionPaid, true);

      expect(rowValue(data, 'Facilities marked as paid')).toBe('5 out of 5');
      expect(findRow(data, 'Facilities marked as paid')?.action).toBeUndefined();
    });

    it('should not show the marked facilities count when the API omits it', () => {
      const data = toTransactionDetails(
        { ...mockFacilityTransactionFee, actualMarkedFacilities: undefined, expectedMarkedFacilities: undefined },
        true,
      );
      const row = findRow(data, 'Facilities marked as paid');

      expect(rowValue(data, 'Facilities marked as paid')).toBe('');
      expect(row?.action).toBeUndefined();
    });

    it('should link the received amount to the change flow only when editable', () => {
      expect(findRow(toTransactionDetails(mockFacilityTransactionFee, true), 'Received amount (GBP)')).toMatchObject({
        change: true,
        changeLink: './received-amount',
      });

      expect(findRow(toTransactionDetails(mockFacilityTransactionFee, false), 'Received amount (GBP)')).toMatchObject({
        change: false,
        changeLink: null,
      });
    });
  });

  describe('refund transaction', () => {
    it('should show the refund details without a due date or received amount', () => {
      const data = toTransactionDetails(mockFacilityTransactionRefund, true);

      expect(sectionHeaders(data)).toEqual(['Details', 'Refund reconciliation']);
      expect(findRow(data, 'Refund letter')).toBeDefined();
      expect(findRow(data, 'Total refund amount (GBP)')).toBeDefined();
      expect(findRow(data, 'Due date')).toBeUndefined();
      expect(findRow(data, 'Received amount (GBP)')).toBeUndefined();
      expect(findRow(data, 'Expected refund amount (GBP)')).toBeDefined();
      expect(rowValue(data, 'Amount allocated to refunded facilities (GBP)')).toBe('0.00');
    });

    it('should offer the mark all as refunded action while facilities are pending', () => {
      const data = toTransactionDetails(
        { ...mockFacilityTransactionRefund, actualMarkedFacilities: 3, expectedMarkedFacilities: 12 },
        true,
      );
      const row = findRow(data, 'Facilities marked as refunded');

      expect(rowValue(data, 'Facilities marked as refunded')).toBe('3 out of 12');
      expect(row?.action).toBe('Mark all as refunded');
      expect(row?.actionLink).toBe('./mark-facilities/all-refunded');
    });

    it('should hide the mark all as refunded action when no facility is expected to be marked', () => {
      const data = toTransactionDetails(mockFacilityTransactionRefund, true);

      expect(rowValue(data, 'Facilities marked as refunded')).toBe('0 out of 0');
      expect(findRow(data, 'Facilities marked as refunded')?.action).toBeUndefined();
    });
  });

  describe('non-regulator user', () => {
    it('should show the marked facilities count without the mark all action', () => {
      const data = toTransactionDetails(mockFacilityTransactionFee, false);

      expect(rowValue(data, 'Facilities marked as paid')).toBe('10 out of 12');
      expect(findRow(data, 'Facilities marked as paid')?.action).toBeUndefined();
    });
  });

  describe('terminated transaction', () => {
    it('should leave the omitted monetary values empty instead of rendering null', () => {
      const data = toTransactionDetails(mockFacilityTransactionTerminated, true);

      expect(rowValue(data, 'Total buy-out amount (GBP)')).toBe('');
      expect(rowValue(data, 'Expected amount (GBP)')).toBe('');
      expect(rowValue(data, 'Received amount (GBP)')).toBe('');
      expect(rowValue(data, 'Amount allocated to paid facilities (GBP)')).toBe('');
    });
  });
});
