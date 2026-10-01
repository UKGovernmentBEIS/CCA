import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router, RouterLink } from '@angular/router';

import { GovukDatePipe } from '@netz/common/pipes';
import { ButtonDirective, GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { SectorLevelPerformanceAccountTemplateDataViewPagesService } from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import {
  getPatReportStatus,
  isFacilityPatYear,
  PatCriteria,
  PatReportItem,
  toPatTargetPeriodYear,
} from '../pat-report-form.provider';

interface PatReportsState {
  patReportItems: PatReportItem[];
  currentPage: number;
  pageSize: number;
  totalItems: number;
}

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 50;

const ACCOUNT_TABLE_COLUMNS: GovukTableColumn[] = [
  { field: 'businessId', header: 'Target unit ID' },
  { field: 'name', header: 'Operator' },
];

const FACILITY_TABLE_COLUMNS: GovukTableColumn[] = [
  { field: 'businessId', header: 'Facility ID' },
  { field: 'name', header: 'Facility site name' },
];

const COMMON_TABLE_COLUMNS: GovukTableColumn[] = [
  { field: 'submissionDate', header: 'Date submitted' },
  { field: 'status', header: 'Status' },
];

@Component({
  selector: 'cca-pat-report-table',
  templateUrl: './pat-report-table.component.html',
  imports: [TableComponent, RouterLink, GovukDatePipe, TitleCasePipe, PaginationComponent, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatReportTableComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly exportService = inject(ReportingExportService);
  private readonly sectorLevelPerformanceAccountTemplateDataViewPagesService = inject(
    SectorLevelPerformanceAccountTemplateDataViewPagesService,
  );

  protected readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  private readonly queryParamMap = toSignal(this.activatedRoute.queryParamMap, {
    initialValue: this.activatedRoute.snapshot.queryParamMap,
  });

  private readonly targetPeriodYear = computed(() =>
    toPatTargetPeriodYear(this.queryParamMap().get('targetPeriodYear')),
  );

  /**
   * TP6 reports are submitted by target unit; CCA3 years are submitted per facility, so the identifying columns differ.
   */
  protected readonly isFacilityReport = computed(() => isFacilityPatYear(this.targetPeriodYear()));

  protected readonly tableColumns = computed<GovukTableColumn[]>(() => [
    ...(this.isFacilityReport() ? FACILITY_TABLE_COLUMNS : ACCOUNT_TABLE_COLUMNS),
    ...COMMON_TABLE_COLUMNS,
  ]);

  protected readonly state = signal<PatReportsState>({
    patReportItems: [],
    currentPage: DEFAULT_PAGE,
    pageSize: DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  constructor() {
    effect((onCleanup) => {
      const queryParamMap = this.queryParamMap();
      this.state.update((state) => ({ ...state, patReportItems: [], totalItems: 0 }));

      if (queryParamMap.get('reportType') !== 'PAT') return;

      const criteria = toPatCriteria(queryParamMap);
      if (!criteria) return;

      if ((criteria.term?.length > 0 && criteria.term.length < 3) || criteria.term?.length > 255) return;

      this.state.update((state) => ({
        ...state,
        currentPage: criteria.pageNumber + 1,
        pageSize: criteria.pageSize,
      }));

      const subscription = untracked(() =>
        this.getReportList(criteria).subscribe((resp) =>
          this.state.update((state) => ({
            ...state,
            patReportItems: resp.items ?? [],
            totalItems: resp.total ?? 0,
          })),
        ),
      );

      onCleanup(() => subscription.unsubscribe());
    });
  }

  onPageChange(page: number) {
    if (page === this.state().currentPage) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.state().pageSize) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  exportToCsv(): void {
    const totalItems = this.state().totalItems;
    const criteria = this.extractCriteria();
    if (totalItems === 0 || !criteria) return;

    this.exportService.exportPatData(this.sectorId, criteria, totalItems);
  }

  private getReportList(criteria: PatCriteria) {
    return isFacilityPatYear(criteria.targetPeriodYear)
      ? this.sectorLevelPerformanceAccountTemplateDataViewPagesService.getSectorFacilityPerformanceAccountTemplateDataReportList(
          this.sectorId,
          criteria,
        )
      : this.sectorLevelPerformanceAccountTemplateDataViewPagesService.getSectorAccountPerformanceAccountTemplateDataReportList(
          this.sectorId,
          criteria,
        );
  }

  private extractCriteria(): PatCriteria | null {
    return toPatCriteria(this.activatedRoute.snapshot.queryParamMap, {
      pageNumber: 0,
      pageSize: this.state().totalItems,
    });
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }
}

function toPatCriteria(
  queryParamMap: ParamMap,
  overrides: Partial<Pick<PatCriteria, 'pageNumber' | 'pageSize'>> = {},
): PatCriteria | null {
  const targetPeriodYear = toPatTargetPeriodYear(queryParamMap.get('targetPeriodYear'));
  if (!targetPeriodYear) return null;

  return {
    term: queryParamMap.get('term'),
    targetPeriodYear,
    status: getPatReportStatus(queryParamMap.get('status')),
    pageNumber: overrides.pageNumber ?? toPositiveInteger(queryParamMap.get('page'), DEFAULT_PAGE) - 1,
    pageSize: overrides.pageSize ?? toPositiveInteger(queryParamMap.get('pageSize'), DEFAULT_PAGE_SIZE),
  };
}

function toPositiveInteger(value: string | null, fallback: number): number {
  const parsed = Number(value);
  return Number.isSafeInteger(parsed) && parsed > 0 ? parsed : fallback;
}
