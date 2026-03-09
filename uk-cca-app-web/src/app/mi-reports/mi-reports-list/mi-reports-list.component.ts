import { ChangeDetectionStrategy, Component, inject, output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EMPTY, map, switchMap, tap } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { TextEllipsisPipe } from '@shared/pipes';

import { CustomMiReportQuery, MiReportsUserDefinedService, MiReportUserDefinedInfoDTO } from 'cca-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { MiReportsExportService } from '../core/mi-reports-export.service';

interface MiReportsListState {
  reports: MiReportUserDefinedInfoDTO[];
  currentPage: number;
  pageSize: number;
  totalItems: number;
}

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-mi-reports-list',
  templateUrl: './mi-reports-list.component.html',
  imports: [TableComponent, RouterLink, PaginationComponent, TextEllipsisPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsListComponent {
  private readonly miReportsUserDefinedService = inject(MiReportsUserDefinedService);
  private readonly miReportsExportService = inject(MiReportsExportService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly exportError = output<string | null>();

  protected readonly tableColumns: GovukTableColumn<MiReportUserDefinedInfoDTO>[] = [
    { field: 'reportName', header: 'Name' },
    { field: 'description', header: 'Description' },
    { field: 'id', header: 'Actions', widthClass: 'govuk-table__header--numeric' },
  ];

  protected readonly state = signal<MiReportsListState>({
    reports: [],
    currentPage: DEFAULT_PAGE,
    pageSize: DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          const page = +(queryParamMap.get('page') || DEFAULT_PAGE);
          const pageSize = +(queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE);

          this.state.update((state) => ({
            ...state,
            currentPage: page,
            pageSize,
          }));

          return this.miReportsUserDefinedService.getAllMiReportsUserDefined(page - 1, pageSize);
        }),
        tap((resp) =>
          this.state.update((state) => ({
            ...state,
            reports: resp.queries ?? [],
            totalItems: resp.total ?? 0,
          })),
        ),
      )
      .subscribe();
  }

  onPageChange(page: number) {
    if (page === this.state().currentPage) return;
    this.router.navigate([], {
      queryParams: { page },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.state().pageSize) return;
    this.router.navigate([], {
      queryParams: { page: 1, pageSize },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }

  exportToExcel(row: MiReportUserDefinedInfoDTO) {
    this.miReportsUserDefinedService
      .getMiReportUserDefinedById(row.id)
      .pipe(
        switchMap((dto) =>
          this.miReportsUserDefinedService
            .generateCustomReport({ sqlQuery: dto.queryDefinition } as CustomMiReportQuery)
            .pipe(map((results) => ({ results, reportName: dto.reportName }))),
        ),
        catchBadRequest(ErrorCodes.REPORT1001, () => {
          this.exportError.emit('Unable to execute query');
          return EMPTY;
        }),
      )
      .subscribe(({ results, reportName }) => {
        this.exportError.emit(null);
        this.miReportsExportService.exportToExcel(results as ExtendedMiReportResult, reportName);
      });
  }
}
