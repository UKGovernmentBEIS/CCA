import { DatePipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { pipe, switchMap, tap } from 'rxjs';

import { CheckboxComponent, CheckboxesComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';
import { HistoryCategory } from '@shared/types';

import { RequestDetailsSearchResults, RequestSearchCriteria, RequestsService } from 'cca-api';

import { type WorkflowHistoryTabState, workflowStatusesMap, workflowTypesMap } from './workflow-history-tab.types';
import {
  WORKFLOW_HISTORY_TAB_FORM_PROVIDER,
  type WorkflowHistoryTabFormModel,
  WorkflowHistoryTabFormProvider,
} from './workflow-history-tab-form.provider';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

@Component({
  selector: 'cca-workflow-history-tab',
  templateUrl: './workflow-history-tab.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    CheckboxComponent,
    CheckboxesComponent,
    PaginationComponent,
    TagComponent,
    KeyValuePipe,
    StatusColorPipe,
    StatusPipe,
    DatePipe,
  ],
  providers: [WorkflowHistoryTabFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryTabComponent {
  private readonly requestsService = inject(RequestsService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly filtersForm = inject<WorkflowHistoryTabFormModel>(WORKFLOW_HISTORY_TAB_FORM_PROVIDER);

  protected readonly sectorId = this.activatedRoute.snapshot.paramMap.get('sectorId');

  protected readonly workflowTypesMap = workflowTypesMap;
  protected readonly workflowStatusesMap = workflowStatusesMap;

  readonly state = signal<WorkflowHistoryTabState>({
    workflowsHistory: null,
    totalItems: 0,
    requestTypes: [],
    requestStatuses: [],
    currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || DEFAULT_PAGE,
    pageSize: +this.activatedRoute.snapshot.queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          const page = +queryParamMap.get('page') || DEFAULT_PAGE;
          const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;
          const requestTypes = queryParamMap.getAll('requestTypes');
          const requestStatuses = queryParamMap.getAll('requestStatuses');

          this.state.update((state) => ({
            ...state,
            currentPage: page,
            pageSize,
            requestTypes,
            requestStatuses,
          }));

          const requestSearchCriteria: RequestSearchCriteria = {
            resourceType: 'SECTOR_ASSOCIATION',
            resourceId: this.sectorId,
            requestTypes,
            requestStatuses,
            historyCategory: HistoryCategory.SECTOR,
            pageNumber: page - 1,
            pageSize,
          };

          return this.fetchWorkflows(requestSearchCriteria);
        }),
        this.updateWorkflows(),
      )
      .subscribe();

    // We need to handle the formValues separately, in order for the queryParams logic to be fully functional
    this.filtersForm.valueChanges
      .pipe(
        takeUntilDestroyed(),
        tap((formValues) =>
          this.handleQueryParamsNavigation({
            requestTypes: formValues.requestTypes,
            requestStatuses: formValues.requestStatuses,
          }),
        ),
      )
      .subscribe();
  }

  onPageChange(page: number) {
    if (page === this.state().currentPage) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.state().pageSize) return;
    this.handleQueryParamsNavigation({ pageSize });
  }

  private fetchWorkflows(requestSearchCriteria: RequestSearchCriteria) {
    return this.requestsService.getRequestDetailsByResource(requestSearchCriteria);
  }

  private updateWorkflows() {
    return pipe(
      tap((results: RequestDetailsSearchResults) =>
        this.state.update((state) => ({
          ...state,
          workflowsHistory: results,
          totalItems: results.total || 0,
        })),
      ),
    );
  }

  private handleQueryParamsNavigation(
    filters: Partial<{
      page: number;
      pageSize: number;
      requestTypes: string[];
      requestStatuses: string[];
    }>,
  ) {
    this.router.navigate([], {
      queryParams: { ...filters },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: this.activatedRoute.snapshot.fragment,
    });
  }
}
