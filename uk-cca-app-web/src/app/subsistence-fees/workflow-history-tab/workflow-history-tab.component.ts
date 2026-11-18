import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink, RouterModule } from '@angular/router';

import { switchMap } from 'rxjs';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { SubsistenceFeesStore } from '../subsistence-fees.store';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-workflow-history-tab',
  templateUrl: './workflow-history-tab.component.html',
  imports: [
    RouterModule,
    RouterLink,
    DatePipe,
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
  private readonly store = inject(SubsistenceFeesStore);

  protected readonly workflowHistoryColumns: GovukTableColumn[] = [
    { field: 'id', header: 'Payment request ID' },
    { field: 'creationDate', header: 'Date initiated' },
    { field: 'requestStatus', header: 'Process status', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'sentInvoices', header: 'Requests sent' },
    { field: 'failedInvoices', header: 'Failed requests' },
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

          return this.store.fetchWorkflows();
        }),
        this.store.updateWorkflows(),
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

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'workflow-history',
    });
  }
}
