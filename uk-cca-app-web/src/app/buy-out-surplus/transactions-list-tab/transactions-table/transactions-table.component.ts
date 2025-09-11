import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { BuyOutAndSurplusTransactionsInfoViewService, BuyOutSurplusTransactionListItemDTO } from 'cca-api';

import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE, extractCriteria } from '../utils';

interface TransactionsTableState {
  transactions: BuyOutSurplusTransactionListItemDTO[];
  currentPage: number;
  pageSize: number;
  totalItems: number;
}

@Component({
  selector: 'cca-transaction-table',
  templateUrl: './transactions-table.component.html',
  standalone: true,
  imports: [TableComponent, RouterLink, GovukDatePipe, PaginationComponent, StatusPipe, StatusColorPipe, DecimalPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionsTableComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly buyOutAndSurplusTransactionsInfoViewService = inject(BuyOutAndSurplusTransactionsInfoViewService);

  protected readonly columns: GovukTableColumn[] = [
    { field: 'accountBusinessId', header: 'Target Unit ID' },
    { field: 'operatorName', header: 'Operator Name' },
    { field: 'transactionCode', header: 'Transaction ID' },
    { field: 'creationDate', header: 'Date sent' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'buyOutFee', header: 'Amount (GBP)' },
  ];

  readonly state = signal<TransactionsTableState>({
    transactions: [],
    currentPage: DEFAULT_PAGE,
    pageSize: DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          const criteria = extractCriteria(queryParamMap);

          this.state.update((state) => ({
            ...state,
            currentPage: criteria.pageNumber + 1,
            pageSize: criteria.pageSize,
          }));

          return this.buyOutAndSurplusTransactionsInfoViewService.getBuyOutSurplusTransactions(criteria);
        }),
        tap((resp) =>
          this.state.update((state) => ({ ...state, transactions: resp?.transactions, totalItems: resp?.total })),
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

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: this.activatedRoute.snapshot.fragment,
    });
  }
}
