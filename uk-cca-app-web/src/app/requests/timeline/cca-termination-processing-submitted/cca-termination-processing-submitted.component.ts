import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { Cca2TerminationAccountProcessingSubmittedRequestActionPayload, FacilityBaseInfoDTO } from 'cca-api';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-cca-termination-processing-submitted',
  imports: [TableComponent, PaginationComponent],
  templateUrl: './cca-termination-processing-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CcaTerminationProcessingSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as Cca2TerminationAccountProcessingSubmittedRequestActionPayload;

  protected readonly facilities = this.actionPayload?.excludedFacilities ?? [];

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);

  protected readonly paginatedItems = computed(() => {
    const startIndex = (this.currentPage() - 1) * this.pageSize();
    const endIndex = startIndex + this.pageSize();
    return this.facilities.slice(startIndex, endIndex);
  });

  protected readonly tableColumns: GovukTableColumn<FacilityBaseInfoDTO>[] = [
    { field: 'siteName', header: 'Site name' },
    { field: 'facilityBusinessId', header: 'Facility ID' },
  ];

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamMap) => {
      const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
      const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;

      this.currentPage.set(pageNumber);
      this.pageSize.set(pageSize);
    });
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(params: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...params },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }
}
