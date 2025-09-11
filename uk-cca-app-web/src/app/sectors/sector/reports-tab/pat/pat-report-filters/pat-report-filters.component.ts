import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import { initialPatValues, PAT_REPORT_FORM, PatReportFormProvider } from '../pat-report-form.provider';

@Component({
  selector: 'cca-pat-report-filters',
  templateUrl: './pat-report-filters.component.html',
  standalone: true,
  imports: [UtilityPanelComponent, TextInputComponent, SelectComponent, ReactiveFormsModule, ButtonDirective],
  providers: [PatReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatReportFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly filtersForm = inject(PAT_REPORT_FORM);

  protected readonly targetPeriodTypeOptions: GovukSelectOption[] = [{ value: 'TP6', text: 'TP6' }];

  protected readonly statusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'SUBMITTED', text: 'Submitted' },
    { value: 'OUTSTANDING', text: 'Outstanding' },
  ];

  protected readonly submissionTypeOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'FINAL', text: 'Final' },
    { value: 'INTERIM', text: 'Interim' },
  ];

  clear() {
    this.filtersForm.reset(initialPatValues);

    this.router.navigate([], {
      queryParams: { reportType: 'PAT', ...this.filtersForm.value, pageNumber: 1 },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }

  apply() {
    this.router.navigate([], {
      queryParams: { reportType: 'PAT', ...this.filtersForm.value, pageNumber: 1 },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }
}
