import { ParamMap } from '@angular/router';

import { SchemeVersion, SchemeVersionEnum } from '@shared/types';

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

export const TargetPeriodTypesBySchemeVersion: Record<
  SchemeVersion,
  readonly BuyOutSurplusTransactionsListSearchCriteria['targetPeriodType'][]
> = {
  [SchemeVersionEnum.CCA_2]: ['TP6'],
  [SchemeVersionEnum.CCA_3]: ['TP7', 'TP8', 'TP9'],
};

export function extractTargetPeriodType(
  queryParamMap: ParamMap,
): BuyOutSurplusTransactionsListSearchCriteria['targetPeriodType'] | null {
  const targetPeriodType = queryParamMap.get('targetPeriodType');
  const schemeTargetPeriodTypes: readonly string[] =
    TargetPeriodTypesBySchemeVersion[extractSchemeVersion(queryParamMap)];

  return targetPeriodType !== null && schemeTargetPeriodTypes.includes(targetPeriodType)
    ? (targetPeriodType as BuyOutSurplusTransactionsListSearchCriteria['targetPeriodType'])
    : null;
}

export function extractTerm(queryParamMap: ParamMap): string | null {
  const term = queryParamMap.get('term');
  if (term && term.length >= 3) return term;
  return null;
}

export function extractSchemeVersion(queryParamMap: ParamMap): SchemeVersion {
  return queryParamMap.get('schemeVersion') === SchemeVersionEnum.CCA_2
    ? SchemeVersionEnum.CCA_2
    : SchemeVersionEnum.CCA_3;
}

export function extractBuyOutSurplusPaymentStatus(
  queryParamMap: ParamMap,
): BuyOutSurplusTransactionsListSearchCriteria['paymentStatus'] | null {
  const paymentStatus = queryParamMap.get('paymentStatus');
  if (!isPaymentStatus(paymentStatus)) return null;
  return paymentStatus;
}

export function isPaymentStatus(
  paymentStatus: string | null,
): paymentStatus is BuyOutSurplusTransactionsListSearchCriteria['paymentStatus'] {
  return paymentStatus !== null && (TransactionStatuses as readonly string[]).includes(paymentStatus);
}

export function extractPagination(
  queryParamMap: ParamMap,
): Pick<BuyOutSurplusTransactionsListSearchCriteria, 'pageNumber' | 'pageSize'> {
  const page = Number(queryParamMap.get('page'));
  const pageSize = Number(queryParamMap.get('pageSize'));

  return {
    pageNumber: Number.isInteger(page) && page >= 1 ? page - 1 : DEFAULT_PAGE - 1,
    pageSize: Number.isInteger(pageSize) && pageSize >= 1 ? pageSize : DEFAULT_PAGE_SIZE,
  };
}

export function extractCriteria(queryParamMap: ParamMap): BuyOutSurplusTransactionsListSearchCriteria {
  return {
    term: extractTerm(queryParamMap),
    targetPeriodType: extractTargetPeriodType(queryParamMap),
    paymentStatus: extractBuyOutSurplusPaymentStatus(queryParamMap),
    schemeVersion: extractSchemeVersion(queryParamMap),
    ...extractPagination(queryParamMap),
  };
}
