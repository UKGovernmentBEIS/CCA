import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { combineLatest, distinctUntilChanged, filter, startWith, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { GovukTableColumn, LinkDirective, TabLazyDirective, TabsComponent } from '@netz/govuk-components';
import { PageHeadingComponent, PaginationComponent } from '@shared/components';

import { ItemTargetUnitDTO } from 'cca-api';

import {
  DashboardStore,
  selectActiveTab,
  selectItems,
  selectPage,
  selectPageSize,
  selectTotal,
  WorkflowItemsAssignmentType,
} from '../../+store';
import { DashboardItemsListComponent } from '../../components/dashboard-items-list';
import { WorkflowItemsService } from '../../services/workflow-items.service';

const DEFAULT_TABLE_COLUMNS: GovukTableColumn<ItemTargetUnitDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: false },
  { field: 'taskAssignee', header: 'Assigned to', isSortable: false },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: false },
  { field: 'businessId', header: `Target unit ID`, isSortable: false },
  { field: 'accountName', header: 'Target unit', isSortable: false },
];

const getTableColumns = (activeTab: WorkflowItemsAssignmentType): GovukTableColumn<ItemTargetUnitDTO>[] => {
  const cols = DEFAULT_TABLE_COLUMNS;

  return cols.filter((column) => {
    return activeTab === 'assigned-to-others' || column.field !== 'taskAssignee';
  });
};

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'cca-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  providers: [WorkflowItemsService, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    NgTemplateOutlet,
    PaginationComponent,
    RouterModule,
    LinkDirective,
    DashboardItemsListComponent,
  ],
})
export class DashboardPageComponent implements OnInit {
  private readonly workflowItemsService = inject(WorkflowItemsService);
  private readonly dashboardStore = inject(DashboardStore);
  private readonly authStore = inject(AuthStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroy$ = inject(DestroySubject);

  protected readonly pageSize = this.dashboardStore.select(selectPageSize)();
  protected readonly isLoading = signal(false);

  protected readonly activeTab$ = toObservable(this.dashboardStore.select(selectActiveTab));
  protected readonly page$ = toObservable(this.dashboardStore.select(selectPage));

  protected readonly role = computed(() => this.authStore.select(selectUserRoleType)());
  protected readonly activeTab = computed(() => this.dashboardStore.select(selectActiveTab)());
  protected readonly items = computed(() => this.dashboardStore.select(selectItems)());
  protected readonly total = computed(() => this.dashboardStore.select(selectTotal)());
  protected readonly page = computed(() => this.dashboardStore.select(selectPage)());
  protected readonly tableColumns = computed(() => getTableColumns(this.activeTab()));

  ngOnInit(): void {
    this.activatedRoute.fragment
      .pipe(
        startWith(this.role() === 'OPERATOR' ? 'assigned-to-others' : 'assigned-to-me'),
        distinctUntilChanged(),
        filter((fragment) => !!fragment),
        takeUntil(this.destroy$),
      )
      .subscribe((tab: WorkflowItemsAssignmentType) => {
        this.dashboardStore.setPage(1);
        this.dashboardStore.setActiveTab(tab);
      });

    combineLatest([this.activeTab$, this.page$])
      .pipe(
        switchMap(([activeTab, page]) => {
          this.isLoading.set(true);
          return this.workflowItemsService.getItems(activeTab, page, this.pageSize);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe(({ items, totalItems }) => {
        this.dashboardStore.setItems(items);
        this.dashboardStore.setTotal(totalItems);
        this.isLoading.set(false);
      });
  }

  changePage(page: number) {
    this.dashboardStore.setPage(page);
  }
}
