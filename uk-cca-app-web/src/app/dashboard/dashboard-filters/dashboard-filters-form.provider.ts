import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { DashboardOrderBy, WorkflowRequestType } from '../+store';
import { DEFAULT_ORDER_BY, isKnownRequestType, isOrderBy, MIN_DASHBOARD_SEARCH_TERM_LENGTH } from '../utils';

export type DashboardFiltersFormValue = {
  searchTerm: string | null;
  requestType: WorkflowRequestType | null;
  orderBy: DashboardOrderBy;
};

export type DashboardFiltersFormModel = FormGroup<{
  searchTerm: FormControl<string | null>;
  requestType: FormControl<WorkflowRequestType | null>;
  orderBy: FormControl<DashboardOrderBy>;
}>;

export const DASHBOARD_FILTERS_FORM = new InjectionToken<DashboardFiltersFormModel>('Dashboard filters form');

export const dashboardInitialValues: DashboardFiltersFormValue = {
  searchTerm: null,
  requestType: null,
  orderBy: DEFAULT_ORDER_BY,
};

export const DashboardFiltersFormProvider: Provider = {
  provide: DASHBOARD_FILTERS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;
    const orderBy = queryParamMap.get('orderBy');
    const requestType = queryParamMap.get('requestType');

    return fb.group({
      searchTerm: fb.control(queryParamMap.get('searchTerm'), {
        validators: [
          GovukValidators.minLength(MIN_DASHBOARD_SEARCH_TERM_LENGTH, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      requestType: fb.control(isKnownRequestType(requestType) ? requestType : null),
      orderBy: fb.control(isOrderBy(orderBy) ? orderBy : DEFAULT_ORDER_BY, { nonNullable: true }),
    });
  },
};
