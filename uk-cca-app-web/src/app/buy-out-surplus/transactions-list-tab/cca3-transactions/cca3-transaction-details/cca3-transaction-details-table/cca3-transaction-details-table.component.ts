import { Component, computed, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, switchMap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { ButtonDirective } from '@netz/govuk-components';
import { CcaTableColumn, PaginationComponent, TableComponent } from '@shared/components';
import { FormatGbpPipe, StatusColorPipe, StatusPipe } from '@shared/pipes';

import { FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO } from 'cca-api';

import { compareFacilityIds, extractTransactionDetailsCriteria } from '../../utils';
import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';

@Component({
  selector: 'cca-cca3-transaction-details-table',
  templateUrl: './cca3-transaction-details-table.component.html',
  imports: [
    TableComponent,
    PaginationComponent,
    StatusPipe,
    StatusColorPipe,
    FormatGbpPipe,
    RouterLink,
    ButtonDirective,
  ],
})
export class Cca3TransactionDetailsTableComponent {
  private readonly router = inject(Router);
  private readonly store = inject(Cca3TransactionDetailsStore);
  protected readonly activatedRoute = inject(ActivatedRoute);

  private readonly userRoleType = inject(AuthStore).select(selectUserRoleType);

  protected readonly isRegulator = computed(() => this.userRoleType() === 'REGULATOR');

  protected readonly tableColumns: CcaTableColumn[] = [
    { field: 'facilityBusinessId', header: 'Facility ID', primary: true, isHeader: true },
    { field: 'siteName', header: 'Site name' },
    { field: 'paymentStatus', header: 'Status' },
    { field: 'buyOutFee', header: 'Amount (GBP)', isNumeric: true },
    { field: 'actions', header: 'Actions', widthClass: 'govuk-!-text-align-right' },
  ];

  protected readonly facilities = this.store.select((state) => state.facilities);
  protected readonly totalFacilities = this.store.select((state) => state.totalFacilities);
  protected readonly selectedFacilities = this.store.select((state) => state.selectedFacilities);
  protected readonly currentPage = this.store.select((state) => state.currentPage);
  protected readonly pageSize = this.store.select((state) => state.pageSize);
  protected readonly loaded = this.store.select((state) => state.loaded);

  protected readonly tableData = computed(() =>
    this.facilities()
      .slice()
      .sort((a, b) => compareFacilityIds(a.facilityBusinessId, b.facilityBusinessId))
      .map((tr) => ({
        ...tr,
        isSelectable: this.isRegulator() && tr.paymentStatus !== 'TERMINATED',
      })),
  );

  protected readonly selectionAnnouncement = computed(() => {
    const count = this.selectedFacilities().size;

    return count === 0
      ? 'No facilities selected. Select at least one facility to change its status'
      : `${count} facilities selected`;
  });

  protected readonly resultsAnnouncement = computed(() => {
    if (!this.loaded()) return '';

    const total = this.totalFacilities();
    if (!total) return 'There are no results';

    const currentPage = this.currentPage();
    const pageSize = this.pageSize();
    const firstResult = (currentPage - 1) * pageSize + 1;

    return `Showing ${firstResult} to ${Math.min(currentPage * pageSize, total)} of ${total} results`;
  });

  constructor() {
    combineLatest([this.activatedRoute.paramMap, this.activatedRoute.queryParamMap])
      .pipe(
        takeUntilDestroyed(),
        switchMap(([paramMap, queryParamMap]) => {
          const criteria = extractTransactionDetailsCriteria(queryParamMap);

          return this.store.fetchFacilities(+paramMap.get('transactionId'), criteria);
        }),
      )
      .subscribe();
  }

  onChangeStatus() {
    this.router.navigate(['change-status'], { relativeTo: this.activatedRoute });
  }

  onSelectChange({
    row,
    checked,
  }: {
    row: FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
    checked: boolean;
  }) {
    this.store.updateSelectedFacilities(row, checked);
  }

  onSelectWholePageChange(checked: boolean) {
    for (const facility of this.facilities()) {
      this.store.updateSelectedFacilities(facility, checked);
    }
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }
}
