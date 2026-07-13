import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router, RouterLink } from '@angular/router';

import { EMPTY, switchMap, tap } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import { ButtonDirective, GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { SectorFacilityPerformanceDataReportItemDTO, SectorLevelPerformanceDataViewPagesService } from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import {
  FacilityPerformanceDataCriteria,
  FacilityTargetPeriodReportType,
  getApplicableSubType,
  getReportStatus,
  getTargetPeriodReportType,
  isFacilityTargetPeriod,
} from '../facility-performance-data-report-form.provider';
import { FacilityPerformanceReportStatusPipe } from '../facility-performance-report-status.pipe';

interface FacilityPerformanceDataReportsState {
  performanceDataReportItems: SectorFacilityPerformanceDataReportItemDTO[];
  currentPage: number;
  pageSize: number;
  totalItems: number;
}

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 50;

const COMMON_TABLE_COLUMNS: GovukTableColumn[] = [
  { field: 'facilityBusinessId', header: 'Facility ID' },
  { field: 'siteName', header: 'Facility site name' },
  { field: 'submissionDate', header: 'Date submitted' },
  { field: 'reportVersion', header: 'Report version' },
  { field: 'reportStatus', header: 'Status' },
];

const FINAL_REPORT_ONLY_COLUMNS: GovukTableColumn[] = [
  { field: 'submissionType', header: 'Subtype' },
  { field: 'locked', header: 'Locked' },
];

const VARIATION_TABLE_COLUMN: GovukTableColumn = { field: 'variationIndicator', header: 'New variation' };

@Component({
  selector: 'cca-facility-performance-data-report-table',
  templateUrl: './facility-performance-data-report-table.component.html',
  imports: [
    TableComponent,
    RouterLink,
    GovukDatePipe,
    FacilityPerformanceReportStatusPipe,
    TitleCasePipe,
    PaginationComponent,
    ButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityPerformanceDataReportTableComponent {
  private readonly sectorLevelPerformanceDataViewPagesService = inject(SectorLevelPerformanceDataViewPagesService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly exportService = inject(ReportingExportService);

  protected readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  private readonly queryParamMap = toSignal(this.activatedRoute.queryParamMap, {
    initialValue: this.activatedRoute.snapshot.queryParamMap,
  });

  private readonly targetPeriodReportType = computed(() => {
    const queryParamMap = this.queryParamMap();

    return getTargetPeriodReportType(
      queryParamMap.get('targetPeriodType'),
      queryParamMap.get('targetPeriodReportType'),
    );
  });

  /**
   * Interim reports have no primary/secondary subtype and are not locked, so those final TPR columns are omitted.
   */
  protected readonly tableColumns = computed<GovukTableColumn[]>(() =>
    getTableColumnsForReportType(this.targetPeriodReportType()),
  );

  protected readonly state = signal<FacilityPerformanceDataReportsState>({
    performanceDataReportItems: [],
    currentPage: DEFAULT_PAGE,
    pageSize: DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          if (queryParamMap.get('reportType') !== 'Performance') return EMPTY;

          const criteria = toFacilityPerformanceDataCriteria(queryParamMap);
          if (!criteria) return EMPTY;

          if (
            criteria.facilityOrTargetUnitAccountBusinessId?.length > 0 &&
            criteria.facilityOrTargetUnitAccountBusinessId?.length < 3
          ) {
            return EMPTY;
          }

          this.state.update((state) => ({
            ...state,
            currentPage: criteria.pageNumber + 1,
            pageSize: criteria.pageSize,
          }));

          return this.sectorLevelPerformanceDataViewPagesService.getSectorFacilityPerformanceDataReportList(
            this.sectorId,
            criteria,
          );
        }),
        tap((resp) =>
          this.state.update((state) => ({
            ...state,
            performanceDataReportItems: resp.performanceDataReportItems ?? [],
            totalItems: resp.total ?? 0,
          })),
        ),
      )
      .subscribe();
  }

  onPageChange(page: number) {
    if (page === this.state().currentPage) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.state().pageSize) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  exportToXlsx(): void {
    const totalItems = this.state().totalItems;
    if (totalItems === 0) return;

    this.exportService.exportFacilityData(this.sectorId, this.extractCriteria(), totalItems);
  }

  private extractCriteria(): FacilityPerformanceDataCriteria {
    return toFacilityPerformanceDataCriteria(this.activatedRoute.snapshot.queryParamMap, {
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

function toFacilityPerformanceDataCriteria(
  queryParamMap: ParamMap,
  overrides: Partial<Pick<FacilityPerformanceDataCriteria, 'pageNumber' | 'pageSize'>> = {},
): FacilityPerformanceDataCriteria | null {
  const targetPeriodType = queryParamMap.get('targetPeriodType');
  if (!isFacilityTargetPeriod(targetPeriodType)) return null;

  const targetPeriodReportType = getTargetPeriodReportType(
    targetPeriodType,
    queryParamMap.get('targetPeriodReportType'),
  );
  const reportStatus = getReportStatus(queryParamMap.get('reportStatus'), targetPeriodReportType);

  return {
    facilityOrTargetUnitAccountBusinessId: queryParamMap.get('facilityOrTargetUnitAccountBusinessId'),
    targetPeriodType,
    targetPeriodReportType,
    reportStatus,
    subType: getApplicableSubType(queryParamMap.get('subType'), targetPeriodReportType, reportStatus),
    pageNumber: overrides.pageNumber ?? (+queryParamMap.get('page') || DEFAULT_PAGE) - 1,
    pageSize: overrides.pageSize ?? (+queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE),
  };
}

function getTableColumnsForReportType(targetPeriodReportType: FacilityTargetPeriodReportType): GovukTableColumn[] {
  if (targetPeriodReportType === 'INTERIM') {
    return [...COMMON_TABLE_COLUMNS, VARIATION_TABLE_COLUMN];
  }

  return [...COMMON_TABLE_COLUMNS, ...FINAL_REPORT_ONLY_COLUMNS, VARIATION_TABLE_COLUMN];
}
