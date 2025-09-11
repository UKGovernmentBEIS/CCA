import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { switchMap, tap } from 'rxjs';

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
  standalone: true,
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

  private readonly queryParams = toSignal(this.activatedRoute.queryParamMap);

  protected readonly state = computed(() => ({
    items: this.dashboardStore.select(selectItems)(),
    activeTab: this.dashboardStore.select(selectActiveTab)(),
    totalItems: this.dashboardStore.select(selectTotal)(),
    isLoading: this.isLoading(),
  }));

  protected readonly role = computed(() => this.authStore.select(selectUserRoleType)());
  protected readonly currentPage = computed(() => +this.queryParams()?.get('page') || DEFAULT_PAGE);
  protected readonly pageSize = computed(() => +this.queryParams()?.get('pageSize') || DEFAULT_PAGE_SIZE);
  protected readonly tableColumns = computed(() => getTableColumns(this.state().activeTab));

  constructor() {
    this.activatedRoute.fragment
      .pipe(
        takeUntilDestroyed(),
        tap(() => this.isLoading.set(true)),
        switchMap((fragment) => {
          const defaultTab = this.role() === 'OPERATOR' ? 'assigned-to-others' : 'assigned-to-me';
          const tab = (fragment || defaultTab) as WorkflowItemsAssignmentType;

          this.dashboardStore.setActiveTab(tab);

          return this.workflowItemsService.getItems(tab, this.currentPage(), this.pageSize());
        }),
        tap(({ items, totalItems }) => {
          this.dashboardStore.setItems(items);
          this.dashboardStore.setTotal(totalItems);
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
