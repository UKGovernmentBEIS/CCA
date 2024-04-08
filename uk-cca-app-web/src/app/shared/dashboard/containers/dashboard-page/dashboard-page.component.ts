import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, distinctUntilChanged, filter, map, Observable, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { DashboardItemsListComponent } from '@shared/dashboard/components';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PaginationComponent } from '@shared/pagination/pagination.component';

import { GovukTableColumn, LinkDirective, TabDirective, TabsComponent } from 'govuk-components';

import { ItemDTO, UserStateDTO } from 'cca-api';

import { WorkflowItemsService } from '../../services';
import {
  DashboardStore,
  selectActiveTab,
  selectItems,
  selectPage,
  selectPageSize,
  selectTotal,
  WorkflowItemsAssignmentType,
} from '../../store';

interface ViewModel {
  role: UserStateDTO['roleType'];
  activeTab: WorkflowItemsAssignmentType;
  tableColumns: GovukTableColumn<ItemDTO>[];
  items: ItemDTO[];
  total: number;
  page: number;
  pageSize: number;
}

const DEFAULT_TABLE_COLUMNS: GovukTableColumn<ItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: false },
  { field: 'taskAssignee', header: 'Assigned to', isSortable: false },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: false },
];

const getTableColumns = (activeTab: WorkflowItemsAssignmentType): GovukTableColumn<ItemDTO>[] => {
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
    TabDirective,
    NgTemplateOutlet,
    PaginationComponent,
    RouterModule,
    LinkDirective,
    AsyncPipe,
    DashboardItemsListComponent
  ],
})
export class DashboardPageComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.authStore.pipe(selectUserRoleType),
    this.store.pipe(selectActiveTab),
    this.store.pipe(selectItems),
    this.store.pipe(selectTotal),
    this.store.pipe(selectPage),
    this.store.pipe(selectPageSize),
  ]).pipe(
    map(([role, activeTab, items, total, page, pageSize]) => ({
      role,
      activeTab,
      tableColumns: getTableColumns(activeTab),
      items,
      total,
      page,
      pageSize,
    })),
  );

  constructor(
    private readonly service: WorkflowItemsService,
    private readonly store: DashboardStore,
    private readonly authStore: AuthStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
  ) { }

  ngOnInit(): void {
    (this.route.fragment as Observable<WorkflowItemsAssignmentType>)
      .pipe(
        distinctUntilChanged(),
        filter((fragment) => !!fragment),
        takeUntil(this.destroy$),
      )
      .subscribe((tab) => {
        this.store.setPage(1);
        this.store.setActiveTab(tab);
      });

    this.vm$
      .pipe(
        map((vm) => ({ activeTab: vm.activeTab, page: vm.page, pageSize: vm.pageSize })),
        distinctUntilChanged((prev, curr) => {
          return prev.activeTab === curr.activeTab && prev.page === curr.page && prev.pageSize === curr.pageSize;
        }),
        switchMap(({ activeTab, page, pageSize }) => {
          return this.service.getItems(activeTab, page, pageSize);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe(({ items, totalItems }) => {
        this.store.setItems(items);
        this.store.setTotal(totalItems);
      });
  }

  addAnotherInstallation(): void {
    this.router.navigate(['/'], { state: { addAnotherInstallation: true } });
  }

  selectTab(selected: string) {
    this.router.navigate([], {
      relativeTo: this.route,
      fragment: selected,
    });
  }

  changePage(page: number) {
    this.store.setPage(page);
  }
}
