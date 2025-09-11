import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { StatusTagColorPipe, StatusTagTextPipe, TASK_STATUS_TAG_MAP } from '@netz/common/pipes';
import { RequestTaskStore } from '@netz/common/store';
import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  GovukValidators,
  SelectComponent,
  SortEvent,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  FacilityItemViewModel,
  TaskItemStatus,
  taskStatusTagMap,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { PaginationComponent, UtilityPanelComponent } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;
const FILTERS_AND_PAGINATION_LIMIT = 10;

@Component({
  selector: 'cca-una-manage-facilities',
  templateUrl: './manage-facilities.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    ReactiveFormsModule,
    RouterLink,
    ButtonDirective,
    PendingButtonDirective,
    PaginationComponent,
    TableComponent,
    StatusPipe,
    StatusTagTextPipe,
    StatusTagColorPipe,
    UtilityPanelComponent,
    SelectComponent,
    TextInputComponent,
  ],
  providers: [{ provide: TASK_STATUS_TAG_MAP, useValue: taskStatusTagMap }, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly statusPipe = inject(StatusPipe);

  protected readonly manageFacilities = this.store.select(underlyingAgreementReviewQuery.selectFacilitiesItems);

  protected readonly filtersAndPaginationLimit = FILTERS_AND_PAGINATION_LIMIT;

  readonly currentPage = signal(DEFAULT_PAGE);
  readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  readonly sorting = signal<SortEvent>({ column: 'name', direction: 'ascending' });
  protected readonly searchTerm = signal<string | null>(null);
  protected readonly workflowStatusFilter = signal<TaskItemStatus>(null);

  protected readonly filteredItems = computed(() => {
    const items = this.manageFacilities();
    const term = this.searchTerm()?.toLowerCase();
    const status = this.workflowStatusFilter();

    return items.filter((item) => {
      const matchesTerm =
        !term || item.name.toLowerCase().includes(term) || item.facilityId.toLowerCase().includes(term);

      const matchesStatus = !status || item.workflowStatus === status;

      return matchesTerm && matchesStatus;
    });
  });

  readonly sortedFacilityItems = computed(() => this.filteredItems().slice().sort(this.onSort(this.sorting())));

  readonly paginatedItems = computed(() => {
    const sorted = this.sortedFacilityItems();
    const startIndex = (this.currentPage() - 1) * this.pageSize();
    const endIndex = startIndex + this.pageSize();
    return sorted.slice(startIndex, endIndex);
  });

  protected readonly workflowStatusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'ACCEPTED', text: this.statusPipe.transform('ACCEPTED') },
    { value: 'REJECTED', text: this.statusPipe.transform('REJECTED') },
    { value: 'UNDECIDED', text: this.statusPipe.transform('UNDECIDED') },
  ];

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'facilityId', header: 'Facility ID', isSortable: true },
    { field: 'status', header: 'Facility status', isSortable: true },
    { field: 'workflowStatus', header: 'Workflow status', isSortable: true },
  ];

  readonly searchForm = new FormGroup({
    term: new FormControl<string | null>(null, [
      GovukValidators.minLength(3, 'Enter at least 3 characters'),
      GovukValidators.maxLength(255, 'Enter up to 255 characters'),
    ]),
    workflowStatus: new FormControl<TaskItemStatus>(null),
  });

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamMap) => {
      const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
      const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;
      const term = queryParamMap.get('term');
      const workflowStatus = queryParamMap.get('workflowStatus') as TaskItemStatus;

      this.currentPage.set(pageNumber);
      this.pageSize.set(pageSize);

      this.searchTerm.set(term);
      this.workflowStatusFilter.set(workflowStatus);

      this.searchForm.patchValue({ term, workflowStatus });
    });
  }

  onSort(sortEvent: SortEvent): (fa: FacilityItemViewModel, fb: FacilityItemViewModel) => number {
    return (fa, fb) => {
      const diff: number = fa[sortEvent.column].localeCompare(fb[sortEvent.column], 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sortEvent.direction === 'ascending' ? 1 : -1);
    };
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: this.searchForm.value.term,
      workflowStatus: this.searchForm.value.workflowStatus,
    });
  }

  onClearFilters() {
    this.searchForm.reset();
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: null,
      workflowStatus: null,
    });
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ pageSize });
  }

  private handleQueryParamsNavigation(
    params: Partial<{
      page: number;
      pageSize: number;
      term: string;
      workflowStatus: TaskItemStatus;
    }>,
  ) {
    this.router.navigate([], {
      queryParams: { ...params },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }
}
