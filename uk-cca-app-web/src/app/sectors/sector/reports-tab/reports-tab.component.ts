import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';

import {
  FacilityTargetPeriodReportType,
  getDefaultTargetPeriodReportType,
  getTargetPeriodReportType,
  isFacilityTargetPeriod,
} from './facility-performance-data/facility-performance-data-report-form.provider';
import { FacilityPerformanceDataReportTableComponent } from './facility-performance-data/facility-performance-data-report-table/facility-performance-data-report-table.component';
import { FacilityPerformanceReportFiltersComponent } from './facility-performance-data/facility-performance-report-filters/facility-performance-report-filters.component';
import { PatReportFiltersComponent } from './pat/pat-report-filters/pat-report-filters.component';
import { PatReportTableComponent } from './pat/pat-report-table/pat-report-table.component';
import { PerformanceDataReportTableComponent } from './performance-data/performance-data-report-table/performance-data-report-table.component';
import { PerformanceReportFiltersComponent } from './performance-data/performance-report-filters/performance-report-filters.component';

type ReportCategory = 'Performance' | 'PAT';
type TargetPeriodType = 'TP6' | 'TP7' | 'TP8' | 'TP9';
type TargetPeriodReportType = FacilityTargetPeriodReportType;

@Component({
  selector: 'cca-reports-tab',
  templateUrl: './reports-tab.component.html',
  imports: [
    ReactiveFormsModule,
    SelectComponent,
    PerformanceReportFiltersComponent,
    PerformanceDataReportTableComponent,
    FacilityPerformanceReportFiltersComponent,
    FacilityPerformanceDataReportTableComponent,
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
    reportType: this.fb.control<ReportCategory | null>(
      toReportCategory(this.activatedRoute.snapshot.queryParams.reportType),
    ),
    targetPeriodType: this.fb.control<TargetPeriodType | null>(
      toTargetPeriodType(this.activatedRoute.snapshot.queryParams.targetPeriodType),
    ),
    targetPeriodReportType: this.fb.control<TargetPeriodReportType | null>(
      toTargetPeriodReportType(
        this.activatedRoute.snapshot.queryParams.targetPeriodType,
        this.activatedRoute.snapshot.queryParams.targetPeriodReportType,
      ),
    ),
  });

  private readonly reportTypeControl = this.reportTypeForm.controls.reportType;
  private readonly targetPeriodTypeControl = this.reportTypeForm.controls.targetPeriodType;
  private readonly targetPeriodReportTypeControl = this.reportTypeForm.controls.targetPeriodReportType;

  private suppressNavigation = false;

  protected readonly reportTypeOptions: GovukSelectOption<ReportCategory | null>[] = [
    { value: null, text: null },
    { value: 'Performance', text: 'Target period' },
    { value: 'PAT', text: 'PAT' },
  ];

  protected readonly targetPeriodTypeOptions: GovukSelectOption<TargetPeriodType | null>[] = [
    { value: null, text: null },
    { value: 'TP6', text: 'TP6' },
    { value: 'TP7', text: 'TP7' },
    { value: 'TP8', text: 'TP8' },
    { value: 'TP9', text: 'TP9' },
  ];

  protected readonly targetPeriodReportTypeOptions = computed<GovukSelectOption<TargetPeriodReportType>[]>(() => {
    const targetPeriodType = this.targetPeriodTypeValue();

    if (targetPeriodType === 'TP7') {
      return [{ value: 'FINAL', text: 'Final' }];
    }

    if (targetPeriodType === 'TP8' || targetPeriodType === 'TP9') {
      return [
        { value: 'INTERIM', text: 'Interim' },
        { value: 'FINAL', text: 'Final' },
      ];
    }

    return [];
  });

  protected readonly reportTypeValue = toSignal(this.reportTypeControl.valueChanges, {
    initialValue: this.reportTypeControl.value,
  });

  protected readonly targetPeriodTypeValue = toSignal(this.targetPeriodTypeControl.valueChanges, {
    initialValue: this.targetPeriodTypeControl.value,
  });

  protected readonly showAccountPerformanceReport = computed(
    () => this.reportTypeValue() === 'Performance' && this.targetPeriodTypeValue() === 'TP6',
  );

  protected readonly showFacilityPerformanceReport = computed(
    () => this.reportTypeValue() === 'Performance' && isFacilityTargetPeriod(this.targetPeriodTypeValue()),
  );

  /**
   * The report type select is shown only when there is a real choice; TP7 facility reports always resolve to Final.
   */
  protected readonly showTargetPeriodReportType = computed(
    () => this.showFacilityPerformanceReport() && this.targetPeriodReportTypeOptions().length > 1,
  );

  constructor() {
    this.reportTypeControl.valueChanges.pipe(takeUntilDestroyed()).subscribe((reportType) => {
      this.runWithoutNavigation(() => {
        this.targetPeriodTypeControl.setValue(null);
        this.targetPeriodReportTypeControl.setValue(null);
      });

      this.navigate({ reportType, page: 1 });
    });

    this.targetPeriodTypeControl.valueChanges.pipe(takeUntilDestroyed()).subscribe((targetPeriodType) => {
      if (this.suppressNavigation) return;

      const targetPeriodReportType = getDefaultTargetPeriodReportType(targetPeriodType);

      this.runWithoutNavigation(() => {
        this.targetPeriodReportTypeControl.setValue(targetPeriodReportType);
      });

      this.navigate({
        reportType: 'Performance',
        targetPeriodType,
        targetPeriodReportType,
        page: 1,
      });
    });

    this.targetPeriodReportTypeControl.valueChanges.pipe(takeUntilDestroyed()).subscribe((targetPeriodReportType) => {
      if (this.suppressNavigation) return;

      const targetPeriodType = this.targetPeriodTypeControl.value;
      if (!isFacilityTargetPeriod(targetPeriodType)) return;

      const resolvedTargetPeriodReportType = getTargetPeriodReportType(targetPeriodType, targetPeriodReportType);

      if (resolvedTargetPeriodReportType !== targetPeriodReportType) {
        this.runWithoutNavigation(() => {
          this.targetPeriodReportTypeControl.setValue(resolvedTargetPeriodReportType);
        });
      }

      this.navigate({
        reportType: 'Performance',
        targetPeriodType,
        targetPeriodReportType: resolvedTargetPeriodReportType,
        page: 1,
      });
    });
  }

  private runWithoutNavigation(callback: () => void) {
    this.suppressNavigation = true;
    callback();
    this.suppressNavigation = false;
  }

  private navigate(queryParams: {
    reportType?: ReportCategory | null;
    targetPeriodType?: TargetPeriodType | null;
    targetPeriodReportType?: TargetPeriodReportType | null;
    page: number;
  }) {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams,
      queryParamsHandling: 'replace',
      fragment: 'reports',
    });
  }
}

function toReportCategory(value: string): ReportCategory | null {
  return value === 'Performance' || value === 'PAT' ? value : null;
}

function toTargetPeriodType(value: string): TargetPeriodType | null {
  return value === 'TP6' || value === 'TP7' || value === 'TP8' || value === 'TP9' ? value : null;
}

function toTargetPeriodReportType(
  targetPeriodType: string,
  targetPeriodReportType: string,
): TargetPeriodReportType | null {
  const resolvedTargetPeriodType = toTargetPeriodType(targetPeriodType);

  if (!isFacilityTargetPeriod(resolvedTargetPeriodType)) return null;

  return getTargetPeriodReportType(resolvedTargetPeriodType, targetPeriodReportType);
}
