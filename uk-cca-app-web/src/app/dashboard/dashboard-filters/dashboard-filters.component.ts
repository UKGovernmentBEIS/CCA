import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { ButtonDirective, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import { getWorkflowFilterOptions, SORT_OPTIONS } from '../+store';
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
  private readonly roleType = inject(AuthStore).select(selectUserRoleType);

  readonly filtersForm = inject(DASHBOARD_FILTERS_FORM);

  protected readonly workflowOptions = computed(() => getWorkflowFilterOptions(this.roleType()));
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
