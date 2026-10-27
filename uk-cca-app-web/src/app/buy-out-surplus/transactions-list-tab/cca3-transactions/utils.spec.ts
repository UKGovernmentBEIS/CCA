import { convertToParamMap } from '@angular/router';

import {
  canMarkAllFacilities,
  extractTransactionDetailsCriteria,
  markedFacilitiesCounts,
  pendingFacilitiesCount,
} from './utils';

describe('extractTransactionDetailsCriteria', () => {
  it('should default the pagination when the query params are absent', () => {
    expect(extractTransactionDetailsCriteria(convertToParamMap({}))).toEqual({
      term: null,
      paymentStatus: null,
      pageNumber: 0,
      pageSize: 50,
    });
  });

  it('should map the one-based page param onto the zero-based pageNumber', () => {
    expect(extractTransactionDetailsCriteria(convertToParamMap({ page: '3', pageSize: '25' }))).toMatchObject({
      pageNumber: 2,
      pageSize: 25,
    });
  });

  it('should ignore a search term shorter than 3 characters', () => {
    expect(extractTransactionDetailsCriteria(convertToParamMap({ term: 'ab' })).term).toBeNull();
  });

  it('should keep a valid search term and payment status', () => {
    expect(
      extractTransactionDetailsCriteria(convertToParamMap({ term: 'Cardiff', paymentStatus: 'AWAITING_PAYMENT' })),
    ).toMatchObject({ term: 'Cardiff', paymentStatus: 'AWAITING_PAYMENT' });
  });

  it('should ignore an unknown payment status', () => {
    expect(
      extractTransactionDetailsCriteria(convertToParamMap({ paymentStatus: 'NOT_A_STATUS' })).paymentStatus,
    ).toBeNull();
  });
});

describe('markedFacilitiesCounts', () => {
  it('should parse the marked and expected facilities counts', () => {
    expect(markedFacilitiesCounts({ expectedMarkedFacilities: 12, actualMarkedFacilities: 10 })).toEqual({
      marked: 10,
      expected: 12,
    });
  });

  it('should return null when the transaction is missing', () => {
    expect(markedFacilitiesCounts(undefined)).toBeNull();
  });
});

describe('pendingFacilitiesCount', () => {
  it.each([
    ['facilities are still pending', { expectedMarkedFacilities: 12, actualMarkedFacilities: 10 }, 2],
    ['a null marked count is treated as zero', { expectedMarkedFacilities: 3, actualMarkedFacilities: null }, 3],
    ['more facilities are marked than expected', { expectedMarkedFacilities: 3, actualMarkedFacilities: 5 }, -2],
    ['the marked count is missing', { expectedMarkedFacilities: 12 }, null],
    ['the expected count is missing', { actualMarkedFacilities: 1 }, null],
  ])('should handle the case where %s', (_, details, expected) => {
    expect(pendingFacilitiesCount(details)).toBe(expected);
  });

  it('should return null when the transaction is missing', () => {
    expect(pendingFacilitiesCount(undefined)).toBeNull();
  });
});

describe('canMarkAllFacilities', () => {
  it('should allow regulators to mark all facilities while any facility is pending', () => {
    expect(canMarkAllFacilities({ expectedMarkedFacilities: 3, actualMarkedFacilities: 0 }, true)).toBe(true);
  });

  it.each([
    ['none is pending', { expectedMarkedFacilities: 3, actualMarkedFacilities: 3 }],
    ['more facilities are marked than expected', { expectedMarkedFacilities: 3, actualMarkedFacilities: 5 }],
    ['the counts are missing', {}],
  ])('should not allow marking all facilities when %s', (_, details) => {
    expect(canMarkAllFacilities(details, true)).toBe(false);
  });

  it('should not allow other users to mark all facilities', () => {
    expect(canMarkAllFacilities({ expectedMarkedFacilities: 3, actualMarkedFacilities: 0 }, false)).toBe(false);
  });
});
