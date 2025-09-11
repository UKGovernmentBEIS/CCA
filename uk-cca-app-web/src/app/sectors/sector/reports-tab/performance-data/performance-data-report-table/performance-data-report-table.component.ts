import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EMPTY, switchMap, tap } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import { ButtonDirective, GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { PerformanceOutcomePipe } from '@shared/pipes';

import { SectorAccountPerformanceDataReportItemDTO, SectorLevelPerformanceDataViewPagesService } from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import { PerformanceDataCriteria } from '../performance-data-report-form.provider';

interface PerformanceDataReportsState {
  performanceDataReportItems: SectorAccountPerformanceDataReportItemDTO[];
  currentPage: number;
  pageSize: number;
  totalItems: number;
}

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 50;

@Component({
  selector: 'cca-performance-data-report-table',
  templateUrl: './performance-data-report-table.component.html',
  standalone: true,
  imports: [
    TableComponent,
    RouterLink,
    GovukDatePipe,
    PerformanceOutcomePipe,
    TitleCasePipe,
    PaginationComponent,
    ButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataReportTableComponent {
  private readonly sectorLevelPerformanceDataViewPagesService = inject(SectorLevelPerformanceDataViewPagesService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly exportService = inject(ReportingExportService);

  protected readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'targetUnitAccountBusinessId', header: 'TU ID' },
    { field: 'operatorName', header: 'Operator' },
    { field: 'submissionDate', header: 'Date submitted' },
    { field: 'reportVersion', header: 'Report version' },
    { field: 'performanceOutcome', header: 'Status' },
    { field: 'submissionType', header: 'Type' },
    { field: 'locked', header: 'Locked' },
  ];

  protected readonly state = signal<PerformanceDataReportsState>({
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

          const criteria: PerformanceDataCriteria = {
            targetUnitAccountBusinessId: queryParamMap.get('targetUnitAccountBusinessId'),
            targetPeriodType: (queryParamMap.get('targetPeriodType') as 'TP5' | 'TP6') ?? 'TP6',
            performanceOutcome: queryParamMap.get(
              'performanceOutcome',
            ) as PerformanceDataCriteria['performanceOutcome'],
            submissionType: queryParamMap.get('submissionType') as PerformanceDataCriteria['submissionType'],
            pageNumber: (+queryParamMap.get('page') || DEFAULT_PAGE) - 1,
            pageSize: +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
          };

          if (criteria?.targetUnitAccountBusinessId?.length > 0 && criteria?.targetUnitAccountBusinessId?.length < 3)
            return;

          this.state.update((state) => ({
            ...state,
            currentPage: criteria.pageNumber + 1,
            pageSize: criteria.pageSize,
          }));

          return this.sectorLevelPerformanceDataViewPagesService.getSectorAccountPerformanceDataReportList(
            this.sectorId,
            criteria,
          );
        }),
        tap((resp) =>
          this.state.update((state) => ({
            ...state,
            performanceDataReportItems: resp.performanceDataReportItems,
            totalItems: resp.total,
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
    this.handleQueryParamsNavigation({ pageSize });
  }

  exportToXlsx(): void {
    this.exportService.exportPerformanceData(this.sectorId, this.extractCriteria(), this.state().totalItems);
  }

  private extractCriteria() {
    const queryParams = this.activatedRoute.snapshot.queryParams;

    return {
      targetUnitAccountBusinessId: queryParams.targetUnitAccountBusinessId,
      performanceOutcome: queryParams.performanceOutcome,
      submissionType: queryParams.submissionType,
      targetPeriodType: queryParams.targetPeriodType,
      pageSize: +queryParams.pageSize,
      pageNumber: +queryParams.page - 1,
    };
  }

  private handleQueryParamsNavigation(
    pagination: Partial<{
      page: number;
      pageSize: number;
      targetUnitAccountBusinessId: PerformanceDataCriteria['targetUnitAccountBusinessId'];
      targetPeriodType: PerformanceDataCriteria['targetPeriodType'];
      performanceOutcome: PerformanceDataCriteria['performanceOutcome'];
      submissionType: PerformanceDataCriteria['submissionType'];
    }>,
  ) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'reports',
    });
  }
}
