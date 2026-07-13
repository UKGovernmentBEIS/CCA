import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonDirective, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import { SORT_OPTIONS, WORKFLOW_FILTER_OPTIONS } from '../+store';
import {
  DASHBOARD_FILTERS_FORM,
  DashboardFiltersFormProvider,
  DashboardFiltersFormValue,
  dashboardInitialValues,
} from './dashboard-filters-form.provider';

type DashboardCriteriaQueryParams = DashboardFiltersFormValue & { page: number };

@Component({
  selector: 'cca-dashboard-filters',
  templateUrl: './dashboard-filters.component.html',
  imports: [ReactiveFormsModule, TextInputComponent, SelectComponent, ButtonDirective, UtilityPanelComponent],
  providers: [DashboardFiltersFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly filtersForm = inject(DASHBOARD_FILTERS_FORM);

  protected readonly workflowOptions = WORKFLOW_FILTER_OPTIONS;
  protected readonly sortOptions = SORT_OPTIONS;

  clear() {
    this.filtersForm.reset(dashboardInitialValues);
    this.handleQueryParamsNavigation({ ...dashboardInitialValues, page: 1 });
  }

  apply() {
    if (this.filtersForm.invalid) {
      this.filtersForm.markAllAsTouched();
      return;
    }

    this.handleQueryParamsNavigation({ ...this.filtersForm.getRawValue(), page: 1 });
  }

  private handleQueryParamsNavigation(criteria: Partial<DashboardCriteriaQueryParams>) {
    this.router.navigate([], {
      queryParams: { ...criteria },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: this.activatedRoute.snapshot.fragment,
    });
  }
}
