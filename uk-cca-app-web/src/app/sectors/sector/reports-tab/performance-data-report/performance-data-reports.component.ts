import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { GovukDatePipe } from '@netz/common/pipes';
import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent, UtilityPanelComponent } from '@shared/components';
import { PerformanceOutcomePipe } from '@shared/pipes';
import { utils, writeFileXLSX } from 'xlsx';

import { SectorAccountsPerformanceReportItemDTO, SectorAssociationTargetPeriodReportingService } from 'cca-api';

import {
  Criteria,
  initialValues,
  PERFORMANCE_DATA_REPORTS_FORM,
  PerformanceDataReportsFormModel,
  PerformanceDataReportsFormProvider,
} from './performance-data-reports-form.provider';

type PerformanceDataReportsState = {
  performanceDataReportItems: SectorAccountsPerformanceReportItemDTO[];
  currentPage: number;
  totalItems: number;
};

@Component({
  selector: 'cca-reports-component',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    SelectComponent,
    TextInputComponent,
    ButtonDirective,
    PendingButtonDirective,
    TableComponent,
    UtilityPanelComponent,
    PaginationComponent,
    RouterLink,
    GovukDatePipe,
    PerformanceOutcomePipe,
    TitleCasePipe,
  ],
  templateUrl: './performance-data-reports.component.html',
  providers: [PerformanceDataReportsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataReportsComponent {
  private readonly sectorAssociationTargetPeriodReportingService = inject(
    SectorAssociationTargetPeriodReportingService,
  );
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly pageSize = 10;

  state = signal<PerformanceDataReportsState>({
    performanceDataReportItems: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || 1,
    totalItems: 0,
  });

  count = computed(() => this.state().totalItems);
  currentPage = computed(() => this.state().currentPage);

  readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  reportTypeForm = this.fb.group({
    reportType: this.fb.control(this.activatedRoute.snapshot.queryParams.reportType),
  });

  readonly filtersForm = inject<PerformanceDataReportsFormModel>(PERFORMANCE_DATA_REPORTS_FORM);

  readonly reportTypeOptions: GovukSelectOption[] = [
    { value: null, text: null },
    { value: 'Performance', text: 'Performance' },
    { value: 'PAT', text: 'PAT' },
  ];

  readonly targetPeriodTypeOptions: GovukSelectOption[] = [{ value: 'TP6', text: 'TP6' }];

  readonly performanceOutcomeOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'TARGET_MET', text: 'Target met' },
    { value: 'BUY_OUT_REQUIRED', text: 'Buy-out required' },
    { value: 'SURPLUS_USED_BUY_OUT_REQUIRED', text: 'Surplus used buy-out required' },
    { value: 'SURPLUS_USED', text: 'Surplus used' },
    { value: 'OUTSTANDING', text: 'Outstanding' },
  ];
  readonly submissionTypeOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'PRIMARY', text: 'Primary' },
    { value: 'SECONDARY', text: 'Secondary' },
  ];
  readonly tableColumns: GovukTableColumn[] = [
    { field: 'targetUnitBusinessId', header: 'TU ID' },
    { field: 'operatorName', header: 'Operator' },
    { field: 'submissionDate', header: 'Date submitted' },
    { field: 'reportVersion', header: 'Report version' },
    { field: 'performanceOutcome', header: 'Status' },
    { field: 'submissionType', header: 'Type' },
    { field: 'locked', header: 'Locked' },
  ];

  readonly reportTypeValue = toSignal(this.reportTypeForm.controls.reportType.valueChanges, {
    initialValue: this.reportTypeForm.controls.reportType.value,
  });

  constructor() {
    effect(() => {
      if (!this.reportTypeValue()) {
        this.filtersForm.reset(initialValues);
      }

      const queryParams = { reportType: this.reportTypeValue() };
      if (this.reportTypeValue() === 'Performance') queryParams['targetPeriodType'] = initialValues.targetPeriodType;

      this.router.navigate([], {
        queryParams,
        queryParamsHandling: this.reportTypeValue() ? 'merge' : 'replace',
        fragment: 'reports',
      });
    });

    this.activatedRoute.queryParams.subscribe((queryParams) => {
      this.fetchPerformanceDataReport(this.sectorId, {
        ...(queryParams as Criteria),
        pageNumber: parseInt(queryParams.page) - 1,
      });
    });
  }

  applyFilters() {
    this.navigateToFilterQuery();
  }

  clearFilters() {
    this.filtersForm.reset(initialValues);
    this.navigateToFilterQuery();
  }

  exportToXlsx() {
    const queryParams = this.activatedRoute.snapshot.queryParams;
    this.sectorAssociationTargetPeriodReportingService
      .getSectorAccountsPerformanceDataReport(this.sectorId, {
        ...(queryParams as Criteria),
        pageNumber: 0,
        pageSize: this.state().totalItems,
      })
      .subscribe((resp) => {
        const obj = resp.performanceReportItems.map((item) => {
          delete item.accountId;
          return item;
        });
        const ws = utils.json_to_sheet(obj);
        const wb = utils.book_new();
        utils.book_append_sheet(wb, ws, 'Data');
        writeFileXLSX(wb, `tp_reporting.xlsx`);
      });
  }

  private fetchPerformanceDataReport(sectorId: number, criteria: Criteria) {
    this.sectorAssociationTargetPeriodReportingService
      .getSectorAccountsPerformanceDataReport(sectorId, {
        performanceOutcome: criteria.performanceOutcome,
        submissionType: criteria.submissionType,
        targetUnitBusinessId: criteria.targetUnitBusinessId,
        targetPeriodType: criteria.targetPeriodType ?? 'TP6',
        pageNumber: isNaN(criteria.pageNumber) ? 0 : criteria.pageNumber,
        pageSize: criteria.pageSize ?? this.pageSize,
      })
      .subscribe((performanceReportDto) => {
        this.state.update((state) => ({
          ...state,
          performanceDataReportItems: performanceReportDto.performanceReportItems,
          totalItems: performanceReportDto.total,
        }));
      });
  }

  private navigateToFilterQuery() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        page: this.state().currentPage,
        ...this.filtersForm.value,
      },
      queryParamsHandling: 'merge',
      fragment: 'reports',
    });
  }
}
