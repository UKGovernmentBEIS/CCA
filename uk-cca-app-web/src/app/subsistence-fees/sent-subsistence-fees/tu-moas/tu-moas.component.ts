import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import {
  SubsistenceFeesMoaSearchResultInfoDTO,
  SubsistenceFeesMoaSearchResults,
  SubsistenceFeesRunInfoViewService,
} from 'cca-api';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

type TargetUnitMoasState = {
  subsistenceFeesMoas: SubsistenceFeesMoaSearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-tu-moas',
  templateUrl: './tu-moas.component.html',
  standalone: true,
  imports: [TableComponent, PaginationComponent, TagComponent, RouterLink, DecimalPipe, StatusColorPipe, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuMoasComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly subsistenceFeesRunInfoViewService = inject(SubsistenceFeesRunInfoViewService);

  private readonly runId = +this.activatedRoute.snapshot.paramMap.get('runId');

  protected readonly subFeesDetails = toSignal(
    this.subsistenceFeesRunInfoViewService.getSubsistenceFeesRunDetailsById(this.runId),
  );

  readonly state = signal<TargetUnitMoasState>({
    subsistenceFeesMoas: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || DEFAULT_PAGE,
    pageSize: +this.activatedRoute.snapshot.paramMap.get('pageSize') || DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'transactionId', header: 'Transaction ID' },
    { field: 'businessId', header: 'Target unit ID' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        tap((params) => {
          this.state.update((state) => ({
            ...state,
            currentPage: +params.get('page') || DEFAULT_PAGE,
            pageSize: +params.get('pageSize') || DEFAULT_PAGE_SIZE,
          }));
        }),
        switchMap(() => this.fetchMoas()),
        tap((results) => this.updateState(results)),
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

  private fetchMoas() {
    return this.subsistenceFeesRunInfoViewService.getSubsistenceFeesRunMoas(this.runId, {
      moaType: 'TARGET_UNIT_MOA',
      pageNumber: this.state().currentPage - 1,
      pageSize: this.state().pageSize,
    });
  }

  private updateState(results: SubsistenceFeesMoaSearchResults) {
    this.state.update((state) => ({
      ...state,
      subsistenceFeesMoas: results.subsistenceFeesMoas,
      totalItems: results.total,
    }));
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'tu-moas',
    });
  }
}
