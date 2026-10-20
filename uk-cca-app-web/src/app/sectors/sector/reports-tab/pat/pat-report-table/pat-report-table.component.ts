import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EMPTY, switchMap, tap } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import { ButtonDirective, GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import {
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorPerformanceAccountTemplateDataReportItemDTO,
} from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import { PatCriteria } from '../pat-report-form.provider';

interface PatReportsState {
  patReportItems: SectorPerformanceAccountTemplateDataReportItemDTO[];
  currentPage: number;
  pageSize: number;
  totalItems: number;
}

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 50;

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

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'targetUnitAccountBusinessId', header: 'TU ID' },
    { field: 'operatorName', header: 'Operator' },
    { field: 'submissionDate', header: 'Date submitted' },
    { field: 'status', header: 'Status' },
    { field: 'submissionType', header: 'Type' },
  ];

  protected readonly state = signal<PatReportsState>({
    patReportItems: [],
    currentPage: DEFAULT_PAGE,
    pageSize: DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          if (queryParamMap.get('reportType') !== 'PAT') return EMPTY;

          const criteria: PatCriteria = {
            targetUnitAccountBusinessId: queryParamMap.get('targetUnitAccountBusinessId'),
            targetPeriodType: (queryParamMap.get('targetPeriodType') as 'TP5' | 'TP6') ?? 'TP6',
            status: queryParamMap.get('status') as PatCriteria['status'],
            submissionType: queryParamMap.get('submissionType') as PatCriteria['submissionType'],
            pageNumber: (+queryParamMap.get('page') || DEFAULT_PAGE) - 1,
            pageSize: +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
          };

          if (criteria?.targetUnitAccountBusinessId?.length > 0 && criteria?.targetUnitAccountBusinessId?.length < 3)
            return EMPTY;

          this.state.update((state) => ({
            ...state,
            currentPage: criteria.pageNumber + 1,
            pageSize: criteria.pageSize,
          }));

          return this.sectorLevelPerformanceAccountTemplateDataViewPagesService.getSectorPerformanceAccountTemplateDataReportList(
            this.sectorId,
            criteria,
          );
        }),
        tap((resp) =>
          this.state.update((state) => ({
            ...state,
            patReportItems: resp.items,
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
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  exportToXlsx(): void {
    const criteria = extractCriteria(this.activatedRoute.snapshot.queryParams);
    this.exportService.exportPatData(this.sectorId, { ...criteria, pageNumber: 0, pageSize: this.state().totalItems });
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

function extractCriteria(queryParams: Record<string, string>): PatCriteria {
  return {
    targetUnitAccountBusinessId: queryParams.targetUnitAccountBusinessId,
    status: queryParams.status,
    submissionType: queryParams.submissionType,
    targetPeriodType: queryParams.targetPeriodType,
    pageSize: queryParams.pageSize ? +queryParams.pageSize : DEFAULT_PAGE_SIZE,
    pageNumber: queryParams.page ? +queryParams.page - 1 : DEFAULT_PAGE - 1,
  } as PatCriteria;
}
