import { ParamMap } from '@angular/router';

import { ItemSearchCriteriaDTO } from 'cca-api';

import {
  DashboardOrderBy,
  DEFAULT_CRITERIA,
  SORT_OPTIONS,
  WORKFLOW_FILTER_OPTIONS,
  WorkflowRequestType,
} from './+store';

export const DEFAULT_ORDER_BY: DashboardOrderBy = DEFAULT_CRITERIA.orderBy ?? 'NEWEST_FIRST';
export const MIN_DASHBOARD_SEARCH_TERM_LENGTH = 3;

export function extractSearchTerm(queryParamMap: ParamMap): string | undefined {
  const searchTerm = queryParamMap.get('searchTerm');
  // Keep this URL guard aligned with DashboardFiltersFormProvider's minLength validator.
  if (searchTerm && searchTerm.length >= MIN_DASHBOARD_SEARCH_TERM_LENGTH) return searchTerm;
  return undefined;
}

export function isOrderBy(orderBy: string | null): orderBy is DashboardOrderBy {
  return SORT_OPTIONS.some((option) => option.value === orderBy);
}

export function isKnownRequestType(requestType: string | null): requestType is WorkflowRequestType {
  return WORKFLOW_FILTER_OPTIONS.some((option) => typeof option.value === 'string' && option.value === requestType);
}

export function extractDashboardCriteria(queryParamMap: ParamMap): ItemSearchCriteriaDTO {
  const orderBy = queryParamMap.get('orderBy');
  const requestType = queryParamMap.get('requestType');

  return {
    orderBy: isOrderBy(orderBy) ? orderBy : DEFAULT_ORDER_BY,
    requestType: isKnownRequestType(requestType) ? requestType : undefined,
    searchTerm: extractSearchTerm(queryParamMap),
  };
}
