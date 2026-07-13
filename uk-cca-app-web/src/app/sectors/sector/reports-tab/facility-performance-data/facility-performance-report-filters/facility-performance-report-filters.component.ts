import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';

import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import {
  FACILITY_PERFORMANCE_DATA_REPORT_FORM,
  FacilityPerformanceDataCriteria,
  facilityPerformanceDataInitialValues,
  FacilityPerformanceDataReportFormModel,
  FacilityPerformanceDataReportFormProvider,
  getApplicableSubType,
  getReportStatus,
  getTargetPeriodReportType,
} from '../facility-performance-data-report-form.provider';

type ReportStatus = FacilityPerformanceDataCriteria['reportStatus'];
type SubType = FacilityPerformanceDataCriteria['subType'];

const FINAL_REPORT_STATUS_OPTIONS: GovukSelectOption<ReportStatus>[] = [
  { value: null, text: 'All' },
  { value: 'TARGET_MET', text: 'Target met' },
  { value: 'TARGET_NOT_MET', text: 'Target not met' },
  { value: 'OUTSTANDING', text: 'Outstanding' },
];

const INTERIM_REPORT_STATUS_OPTIONS: GovukSelectOption<ReportStatus>[] = [
  { value: null, text: 'All' },
  { value: 'SUBMITTED', text: 'Submitted' },
  { value: 'OUTSTANDING', text: 'Outstanding' },
];

@Component({
  selector: 'cca-facility-performance-report-filters',
  templateUrl: './facility-performance-report-filters.component.html',
  imports: [SelectComponent, ButtonDirective, ReactiveFormsModule, UtilityPanelComponent, TextInputComponent],
  providers: [FacilityPerformanceDataReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityPerformanceReportFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly queryParamMap = toSignal(this.activatedRoute.queryParamMap, {
    initialValue: this.activatedRoute.snapshot.queryParamMap,
  });

  readonly filtersForm = inject<FacilityPerformanceDataReportFormModel>(FACILITY_PERFORMANCE_DATA_REPORT_FORM);

  private readonly targetPeriodReportType = computed(() => {
    const queryParamMap = this.queryParamMap();

    return getTargetPeriodReportType(
      queryParamMap.get('targetPeriodType'),
      queryParamMap.get('targetPeriodReportType'),
    );
  });

  protected readonly reportStatusOptions = computed(() =>
    this.targetPeriodReportType() === 'INTERIM' ? INTERIM_REPORT_STATUS_OPTIONS : FINAL_REPORT_STATUS_OPTIONS,
  );

  /**
   * Subtype is only meaningful for final TPR reports; interim reports display submitted/outstanding status only.
   */
  protected readonly showSubTypeFilter = computed(() => this.targetPeriodReportType() !== 'INTERIM');

  protected readonly subTypeOptions: GovukSelectOption<SubType>[] = [
    { value: null, text: 'All' },
    { value: 'PRIMARY', text: 'Primary' },
    { value: 'SECONDARY', text: 'Secondary' },
  ];

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamMap) => {
      const targetPeriodReportType = getTargetPeriodReportType(
        queryParamMap.get('targetPeriodType'),
        queryParamMap.get('targetPeriodReportType'),
      );
      const reportStatus = getReportStatus(queryParamMap.get('reportStatus'), targetPeriodReportType);

      this.filtersForm.patchValue(
        {
          facilityOrTargetUnitAccountBusinessId: queryParamMap.get('facilityOrTargetUnitAccountBusinessId'),
          reportStatus,
          subType: getApplicableSubType(queryParamMap.get('subType'), targetPeriodReportType, reportStatus),
        },
        { emitEvent: false },
      );
    });

    this.filtersForm.controls.reportStatus.valueChanges.pipe(takeUntilDestroyed()).subscribe((reportStatus) => {
      if (this.targetPeriodReportType() === 'INTERIM' || reportStatus === 'OUTSTANDING') {
        this.filtersForm.controls.subType.setValue(null);
      }
    });
  }

  clear() {
    this.filtersForm.reset(facilityPerformanceDataInitialValues);

    this.handleQueryParamsNavigation({ reportType: 'Performance', ...facilityPerformanceDataInitialValues, page: 1 });
  }

  apply() {
    if (this.filtersForm.invalid) return;

    const values = this.filtersForm.value;
    const targetPeriodReportType = this.targetPeriodReportType();

    this.handleQueryParamsNavigation({
      reportType: 'Performance',
      facilityOrTargetUnitAccountBusinessId: values.facilityOrTargetUnitAccountBusinessId,
      reportStatus: values.reportStatus,
      subType: getApplicableSubType(values.subType, targetPeriodReportType, values.reportStatus),
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
