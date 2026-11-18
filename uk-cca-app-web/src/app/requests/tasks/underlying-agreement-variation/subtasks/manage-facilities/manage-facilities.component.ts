import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { StatusTagColorPipe, StatusTagTextPipe, TASK_STATUS_TAG_MAP } from '@netz/common/pipes';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  GovukSelectOption,
  GovukTableColumn,
  GovukValidators,
  SelectComponent,
  SortEvent,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  atLeastOneActiveFacilityValidator,
  FacilityItemViewModel,
  staticVariationSections,
  TaskItemStatus,
  taskStatusTagMap,
  underlyingAgreementQuery,
} from '@requests/common';
import { PaginationComponent, UtilityPanelComponent } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;
const FILTERS_AND_PAGINATION_LIMIT = 10;

export type FacilityStatus = 'NEW' | 'LIVE' | 'EXCLUDED' | null;

@Component({
  selector: 'cca-manage-facilities',
  templateUrl: './manage-facilities.component.html',
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
    ErrorSummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [{ provide: TASK_STATUS_TAG_MAP, useValue: taskStatusTagMap }, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly statusPipe = inject(StatusPipe);

  protected readonly manageFacilities = this.store.select(underlyingAgreementQuery.selectFacilityItems);
  protected readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable);

  protected readonly isCompleted: Signal<boolean> = computed(() => {
    const sectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const facilitySections = Object.keys(sectionsCompleted).filter(
      (section) => !staticVariationSections.includes(section),
    );

    if (facilitySections.length === 0) throw new Error('No facility found.');

    return facilitySections.every((section) => sectionsCompleted?.[section] === TaskItemStatus.COMPLETED);
  });

  protected readonly filtersAndPaginationLimit = FILTERS_AND_PAGINATION_LIMIT;

  readonly currentPage = signal(DEFAULT_PAGE);
  readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  readonly sorting = signal<SortEvent>({ column: 'name', direction: 'ascending' });
  protected readonly searchTerm = signal<string | null>(null);
  protected readonly statusFilter = signal<FacilityStatus>(null);
  protected readonly workflowStatusFilter = signal<TaskItemStatus>(null);

  protected readonly filteredItems = computed(() => {
    const items = this.manageFacilities();
    const term = this.searchTerm()?.toLowerCase();
    const status = this.statusFilter();
    const workflowStatus = this.workflowStatusFilter();

    return items.filter((item) => {
      const matchesTerm =
        !term || item.name.toLowerCase().includes(term) || item.facilityId.toLowerCase().includes(term);

      const matchesStatus = !status || item.status === status;
      const matchesWorkflowStatus = !workflowStatus || item.workflowStatus === workflowStatus;

      return matchesTerm && matchesWorkflowStatus && matchesStatus;
    });
  });

  readonly sortedFacilityItems = computed(() => this.filteredItems().slice().sort(this.onSort(this.sorting())));

  readonly paginatedItems = computed(() => {
    const sorted = this.sortedFacilityItems();
    const startIndex = (this.currentPage() - 1) * this.pageSize();
    const endIndex = startIndex + this.pageSize();
    return sorted.slice(startIndex, endIndex);
  });

  protected readonly facilityStatusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'NEW', text: this.statusPipe.transform('NEW') },
    { value: 'LIVE', text: this.statusPipe.transform('LIVE') },
    { value: 'EXCLUDED', text: this.statusPipe.transform('EXCLUDED') },
  ];

  protected readonly workflowStatusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.statusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.statusPipe.transform('COMPLETED') },
  ];

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'facilityId', header: 'Facility ID', isSortable: true },
    { field: 'status', header: 'Facility status', isSortable: true },
    { field: 'workflowStatus', header: 'Workflow status', isSortable: true },
    { field: 'links', header: 'Actions' },
  ];

  readonly searchForm = new FormGroup({
    term: new FormControl<string | null>(null, [
      GovukValidators.minLength(3, 'Enter at least 3 characters'),
      GovukValidators.maxLength(255, 'Enter up to 255 characters'),
    ]),
    status: new FormControl<FacilityStatus>(null),
    workflowStatus: new FormControl<TaskItemStatus>(null),
  });

  protected readonly facilitiesForm = new FormGroup({
    facilities: new FormControl(this.manageFacilities() ?? [], { validators: [atLeastOneActiveFacilityValidator()] }),
  });

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamMap) => {
      const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
      const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;
      const term = queryParamMap.get('term');
      const status = queryParamMap.get('status') as FacilityStatus;
      const workflowStatus = queryParamMap.get('workflowStatus') as TaskItemStatus;

      this.currentPage.set(pageNumber);
      this.pageSize.set(pageSize);

      this.searchTerm.set(term);
      this.statusFilter.set(status);
      this.workflowStatusFilter.set(workflowStatus);

      this.searchForm.patchValue({ term, status: status, workflowStatus });
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
      status: this.searchForm.value.status,
      workflowStatus: this.searchForm.value.workflowStatus,
    });
  }

  onClearFilters() {
    this.searchForm.reset();
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: null,
      status: null,
      workflowStatus: null,
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

  private handleQueryParamsNavigation(
    params: Partial<{
      page: number;
      pageSize: number;
      term: string;
      status: FacilityStatus;
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
