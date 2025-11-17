import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';

import { PatReportFiltersComponent } from './pat/pat-report-filters/pat-report-filters.component';
import { PatReportTableComponent } from './pat/pat-report-table/pat-report-table.component';
import { PerformanceDataReportTableComponent } from './performance-data/performance-data-report-table/performance-data-report-table.component';
import { PerformanceReportFiltersComponent } from './performance-data/performance-report-filters/performance-report-filters.component';

@Component({
  selector: 'cca-reports-tab',
  templateUrl: './reports-tab.component.html',
  imports: [
    ReactiveFormsModule,
    SelectComponent,
    PerformanceReportFiltersComponent,
    PerformanceDataReportTableComponent,
    PatReportFiltersComponent,
    PatReportTableComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportsTabComponent {
  private readonly fb = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  protected readonly reportTypeForm = this.fb.group({
    reportType: this.fb.control(this.activatedRoute.snapshot.queryParams.reportType),
  });

  protected readonly reportTypeOptions: GovukSelectOption[] = [
    { value: null, text: null },
    { value: 'Performance', text: 'Performance' },
    { value: 'PAT', text: 'PAT' },
  ];

  protected readonly reportTypeValue = toSignal(this.reportTypeForm.get('reportType').valueChanges, {
    initialValue: this.reportTypeForm.get('reportType').value,
  });

  constructor() {
    effect(() => {
      this.reportTypeForm.get('reportType').valueChanges.subscribe((newReportType) => {
        this.router.navigate([], {
          relativeTo: this.activatedRoute,
          queryParams: { reportType: newReportType, page: 1 },
          queryParamsHandling: 'replace',
          fragment: 'reports',
        });
      });
    });
  }
}
