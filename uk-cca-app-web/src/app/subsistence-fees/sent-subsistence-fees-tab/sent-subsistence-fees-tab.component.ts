import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { switchMap } from 'rxjs';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { SubsistenceFeesStore } from '../subsistence-fees.store';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-sent-subsistence-fees-tab',
  templateUrl: './sent-subsistence-fees-tab.component.html',
  standalone: true,
  imports: [
    RouterLink,
    DatePipe,
    DecimalPipe,
    TableComponent,
    TagComponent,
    PaginationComponent,
    StatusColorPipe,
    StatusPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SentSubsistenceFeesTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(SubsistenceFeesStore);

  protected readonly sentSubsistenceFeesColumns: GovukTableColumn[] = [
    { field: 'paymentRequestId', header: 'Payment request ID' },
    { field: 'submissionDate', header: 'Request date' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  protected readonly state = this.store.stateAsSignal;

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          this.store.updateState({
            currentPage: +queryParamMap.get('page') || DEFAULT_PAGE,
            pageSize: +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
          });

          return this.store.fetchSubsistenceFeesRun();
        }),
        this.store.updateSubsistenceFees(),
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

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'sent-subsistence-fees',
    });
  }
}
