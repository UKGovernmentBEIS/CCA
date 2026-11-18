import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink, RouterModule } from '@angular/router';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { BuyoutSurplusStore, DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from '../buy-out-surplus.store';

@Component({
  selector: 'cca-workflow-history-tab',
  templateUrl: './workflow-history-tab.component.html',
  imports: [
    RouterModule,
    RouterLink,
    DatePipe,
    DecimalPipe,
    TableComponent,
    TagComponent,
    PaginationComponent,
    StatusPipe,
    StatusColorPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyoutSurplusStore = inject(BuyoutSurplusStore);

  protected readonly workflowHistoryColumns: GovukTableColumn[] = [
    { field: 'id', header: 'Run ID' },
    { field: 'creationDate', header: 'Date initiated' },
    { field: 'requestStatus', header: 'Process status', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'totalAccounts', header: 'Total TUs' },
    { field: 'failedAccounts', header: 'Failed TUs' },
  ];

  protected readonly state = this.buyoutSurplusStore.stateAsSignal;

  protected readonly currentPage = computed(() => this.state().currentPage);
  protected readonly pageSize = computed(() => this.state().pageSize);
  protected readonly workflowsHistory = computed(() => this.state().workflowsHistory);
  protected readonly count = computed(() => this.state().totalWorkflowHistoryItems);

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamsMap) => {
      this.buyoutSurplusStore.updateState({
        currentPage: +queryParamsMap.get('page') || DEFAULT_PAGE,
        pageSize: +queryParamsMap.get('pageSize') || DEFAULT_PAGE_SIZE,
      });

      this.buyoutSurplusStore.fetchAndSetWorkflows();
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

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'workflow-history',
    });
  }
}
