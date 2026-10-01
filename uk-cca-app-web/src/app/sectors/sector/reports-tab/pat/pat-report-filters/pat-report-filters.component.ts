import { ChangeDetectionStrategy, Component, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';

import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import {
  getPatReportStatus,
  isFacilityPatYear,
  PAT_REPORT_FORM,
  patInitialValues,
  PatReportFormModel,
  PatReportFormProvider,
  PatReportStatus,
  toPatTargetPeriodYear,
} from '../pat-report-form.provider';

@Component({
  selector: 'cca-pat-report-filters',
  templateUrl: './pat-report-filters.component.html',
  imports: [UtilityPanelComponent, TextInputComponent, SelectComponent, ReactiveFormsModule, ButtonDirective],
  providers: [PatReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatReportFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly queryParamMap = toSignal(this.activatedRoute.queryParamMap, {
    initialValue: this.activatedRoute.snapshot.queryParamMap,
  });

  readonly filtersForm = inject<PatReportFormModel>(PAT_REPORT_FORM);

  private readonly targetPeriodYear = computed(() =>
    toPatTargetPeriodYear(this.queryParamMap().get('targetPeriodYear')),
  );

  /**
   * CCA3 years report per facility, so the search covers facility ids as well as the target unit they sit under.
   */
  protected readonly termLabel = computed(() =>
    isFacilityPatYear(this.targetPeriodYear()) ? 'Facility ID or TU ID' : 'TU ID',
  );

  protected readonly statusOptions: GovukSelectOption<PatReportStatus>[] = [
    { value: null, text: 'All' },
    { value: 'SUBMITTED', text: 'Submitted' },
    { value: 'OUTSTANDING', text: 'Outstanding' },
  ];

  constructor() {
    effect(() => {
      const queryParamMap = this.queryParamMap();

      this.filtersForm.patchValue(
        {
          term: queryParamMap.get('term')?.trim() || null,
          status: getPatReportStatus(queryParamMap.get('status')),
        },
        { emitEvent: false },
      );
    });
  }

  clear() {
    this.filtersForm.reset(patInitialValues);

    this.handleQueryParamsNavigation({ reportType: 'PAT', ...patInitialValues, page: 1 });
  }

  apply() {
    if (this.filtersForm.invalid) return;

    const values = this.filtersForm.value;

    this.handleQueryParamsNavigation({
      reportType: 'PAT',
      term: values.term,
      status: values.status,
      page: 1,
    });
  }

  private handleQueryParamsNavigation(queryParams: Params) {
    this.router.navigate([], {
      queryParams,
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }
}
