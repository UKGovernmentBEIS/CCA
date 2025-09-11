import { ParamMap } from '@angular/router';

import { BuyOutSurplusTransactionsListSearchCriteria } from 'cca-api';

export const DEFAULT_PAGE = 1;
export const DEFAULT_PAGE_SIZE = 50;

export const TransactionStatuses = [
  'AWAITING_PAYMENT',
  'AWAITING_REFUND',
  'PAID',
  'REFUNDED',
  'NOT_REQUIRED',
  'UNDER_APPEAL',
  'TERMINATED',
] as const;

export type TransactionsCriteria = BuyOutSurplusTransactionsListSearchCriteria;

export function extractTargetPeriodType(queryParamMap: ParamMap) {
  const targetPeriodType = queryParamMap.get('targetPeriodType');

  switch (targetPeriodType) {
    case 'TP5':
      return 'TP5';
    case 'TP6':
      return 'TP6';
    default:
      return 'TP6';
  }
}

export function extractTerm(queryParamMap: ParamMap): string | null {
  const term = queryParamMap.get('term');
  if (term && term.length >= 3) return term;
  return null;
}

export function extractBuyOutSurplusPaymentStatus(
  queryParamMap: ParamMap,
): TransactionsCriteria['buyOutSurplusPaymentStatus'] | null {
  const paymentStatus = queryParamMap.get('buyOutSurplusPaymentStatus');
  if (!isPaymentStatus(paymentStatus)) return null;
  return paymentStatus;
}

export function isPaymentStatus(
  paymentStatus: string | null,
): paymentStatus is TransactionsCriteria['buyOutSurplusPaymentStatus'] {
  return TransactionStatuses.includes(paymentStatus as any);
}

export function extractCriteria(queryParamMap: ParamMap): TransactionsCriteria {
  return {
    term: extractTerm(queryParamMap),
    targetPeriodType: extractTargetPeriodType(queryParamMap),
    buyOutSurplusPaymentStatus: extractBuyOutSurplusPaymentStatus(queryParamMap),
    pageNumber: (+queryParamMap.get('page') || DEFAULT_PAGE) - 1,
    pageSize: +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
  };
}
