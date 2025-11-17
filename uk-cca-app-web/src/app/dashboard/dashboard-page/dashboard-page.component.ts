import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { GovukTableColumn, TabLazyDirective, TabsComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { CcaItemDTO } from 'cca-api';

import {
  DashboardStore,
  DEFAULT_PAGE,
  DEFAULT_PAGE_SIZE,
  DEFAULT_TABLE_COLUMNS,
  selectActiveTab,
  selectItems,
  selectTotal,
  WorkflowItemsAssignmentType,
} from '../+store';
import { DashboardItemsListComponent } from '../dashboard-items-list';
import { WorkflowItemsService } from '../workflow-items.service';

const getTableColumns = (activeTab: WorkflowItemsAssignmentType): GovukTableColumn<CcaItemDTO>[] => {
  const cols = DEFAULT_TABLE_COLUMNS;

  return cols.filter((column) => {
    return activeTab === 'assigned-to-others' || column.field !== 'taskAssignee';
  });
};

@Component({
  selector: 'cca-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    NgTemplateOutlet,
    PaginationComponent,
    RouterModule,
    DashboardItemsListComponent,
  ],
  providers: [WorkflowItemsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardPageComponent {
  private readonly workflowItemsService = inject(WorkflowItemsService);
  private readonly dashboardStore = inject(DashboardStore);
  private readonly authStore = inject(AuthStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly isLoading = signal(false);

  protected readonly state = computed(() => ({
    items: this.dashboardStore.select(selectItems)(),
    activeTab: this.dashboardStore.select(selectActiveTab)(),
    totalItems: this.dashboardStore.select(selectTotal)(),
    isLoading: this.isLoading(),
  }));

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  protected readonly role = computed(() => this.authStore.select(selectUserRoleType)());
  protected readonly tableColumns = computed(() => getTableColumns(this.state().activeTab));

  constructor() {
    combineLatest([this.activatedRoute.fragment, this.activatedRoute.queryParamMap])
      .pipe(
        takeUntilDestroyed(),
        tap(() => this.isLoading.set(true)),
        switchMap(([fragment, params]) => {
          const defaultTab = this.role() === 'OPERATOR' ? 'assigned-to-others' : 'assigned-to-me';
          const tab = (fragment || defaultTab) as WorkflowItemsAssignmentType;
          const page = +params.get('page') || this.currentPage();
          const pageSize = +params.get('pageSize') || this.pageSize();

          this.dashboardStore.setActiveTab(tab);
          this.currentPage.set(page);
          this.pageSize.set(pageSize);

          return this.workflowItemsService.getItems(tab, page, pageSize);
        }),
        tap(({ items, totalItems }) => {
          this.dashboardStore.setItems(items || []);
          this.dashboardStore.setTotal(totalItems || 0);
          this.isLoading.set(false);
        }),
      )
      .subscribe();
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
      fragment: this.activatedRoute.snapshot.fragment,
    });
  }
}
