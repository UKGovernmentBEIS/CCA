import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import {
  PERFORMANCE_DATA_REPORT_FORM,
  performanceDataInitialValues,
  PerformanceDataReportFormModel,
  PerformanceDataReportFormProvider,
} from '../performance-data-report-form.provider';

@Component({
  selector: 'cca-performance-report-filters',
  templateUrl: './performance-report-filters.component.html',
  standalone: true,
  imports: [SelectComponent, ButtonDirective, ReactiveFormsModule, UtilityPanelComponent, TextInputComponent],
  providers: [PerformanceDataReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceReportFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly filtersForm = inject<PerformanceDataReportFormModel>(PERFORMANCE_DATA_REPORT_FORM);

  protected readonly targetPeriodTypeOptions: GovukSelectOption[] = [{ value: 'TP6', text: 'TP6' }];

  protected readonly performanceOutcomeOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'TARGET_MET', text: 'Target met' },
    { value: 'BUY_OUT_REQUIRED', text: 'Buy-out required' },
    { value: 'SURPLUS_USED_BUY_OUT_REQUIRED', text: 'Surplus used buy-out required' },
    { value: 'SURPLUS_USED', text: 'Surplus used' },
    { value: 'OUTSTANDING', text: 'Outstanding' },
  ];

  protected readonly submissionTypeOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'PRIMARY', text: 'Primary' },
    { value: 'SECONDARY', text: 'Secondary' },
  ];

  clear() {
    this.filtersForm.reset(performanceDataInitialValues);

    this.router.navigate([], {
      queryParams: { reportType: 'Performance', ...this.filtersForm.value },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }

  apply() {
    this.router.navigate([], {
      queryParams: { reportType: 'Performance', ...this.filtersForm.value },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }
}
