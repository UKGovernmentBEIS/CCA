import { ParamMap } from '@angular/router';

import { FacilityBuyOutSurplusTransactionDetailsSearchCriteria, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { extractBuyOutSurplusPaymentStatus, extractPagination, extractTerm } from '../utils';

/**
 * Maps the URL query params onto the criteria accepted by the facility transaction details endpoint.
 */
export function extractTransactionDetailsCriteria(
  queryParamMap: ParamMap,
): FacilityBuyOutSurplusTransactionDetailsSearchCriteria {
  return {
    term: extractTerm(queryParamMap),
    paymentStatus: extractBuyOutSurplusPaymentStatus(queryParamMap),
    ...extractPagination(queryParamMap),
  };
}

/**
 * Sorts facility IDs ascending, comparing numeric parts numerically (F9 before F10). Mirrors the comparator the other
 * facility lists use.
 */
export const compareFacilityIds = (a: string, b: string): number =>
  (a ?? '').localeCompare(b ?? '', 'en-GB', { numeric: true, sensitivity: 'base' });

export function markedFacilitiesCounts(
  details: FacilityBuyOutSurplusTransactionDTO,
): { marked: number; expected: number } | null {
  const marked = Number(details?.actualMarkedFacilities);
  const expected = Number(details?.expectedMarkedFacilities);

  if (!Number.isFinite(marked) || !Number.isFinite(expected)) return null;

  return { marked, expected };
}

export function pendingFacilitiesCount(details: FacilityBuyOutSurplusTransactionDTO): number | null {
  const counts = markedFacilitiesCounts(details);
  return counts ? counts.expected - counts.marked : null;
}

export function canMarkAllFacilities(details: FacilityBuyOutSurplusTransactionDTO, isRegulator: boolean): boolean {
  return isRegulator && pendingFacilitiesCount(details) > 0;
}
