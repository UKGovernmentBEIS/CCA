import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { PaginationComponent } from '@shared/components';

import { ItemsAssignedToOthersService } from 'cca-api';

import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE, DEFAULT_TABLE_COLUMNS } from '../+store';
import { DashboardItemsListComponent } from '../dashboard-items-list';

@Component({
  selector: 'cca-assigned-to-others',
  templateUrl: './assigned-to-others.component.html',
  imports: [DashboardItemsListComponent, PaginationComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignedToOthersComponent {
  private readonly itemsAssignedToOthersService = inject(ItemsAssignedToOthersService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly state = signal({
    items: [],
    totalItems: 0,
    isLoading: false,
  });

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  protected readonly tableColumns = signal(DEFAULT_TABLE_COLUMNS);

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        tap(() => this.state.set({ ...this.state(), isLoading: true })),
        switchMap((params) => {
          const page = +params.get('page') || this.currentPage();
          const pageSize = +params.get('pageSize') || this.pageSize();

          this.currentPage.set(page);
          this.pageSize.set(pageSize);

          return this.itemsAssignedToOthersService.getAssignedToOthersItems(page - 1, pageSize);
        }),
        tap(({ items, totalItems }) => this.state.set({ items, totalItems, isLoading: false })),
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
