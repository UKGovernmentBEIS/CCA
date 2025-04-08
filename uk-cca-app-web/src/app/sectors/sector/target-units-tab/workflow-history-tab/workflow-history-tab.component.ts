import { DatePipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, type OnInit, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { CheckboxComponent, CheckboxesComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { RequestStatusTagColorPipe, WorkflowStatusPipe } from '@shared/pipes';
import { HistoryCategory } from '@shared/types';

import { type RequestSearchCriteria, RequestsService } from 'cca-api';

import {
  originalOrder,
  type WorkflowHistoryTabState,
  workflowStatusesMap,
  workflowTypesMap,
} from './workflow-history-tab.types';
import {
  WORKFLOW_HISTORY_TAB_FORM_PROVIDER,
  type WorkflowHistoryTabFormModel,
  WorkflowHistoryTabFormProvider,
} from './workflow-history-tab-form.provider';

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
    RequestStatusTagColorPipe,
    WorkflowStatusPipe,
    DatePipe,
  ],
  providers: [WorkflowHistoryTabFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryTabComponent implements OnInit {
  private readonly requestsService = inject(RequestsService);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly filtersForm = inject<WorkflowHistoryTabFormModel>(WORKFLOW_HISTORY_TAB_FORM_PROVIDER);

  readonly sectorId = this.activatedRoute.snapshot.paramMap.get('sectorId');
  readonly pageSize = 30;

  readonly state = signal<WorkflowHistoryTabState>({
    currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || 1,
    workflowsHistory: null,
    totalItems: 0,
    requestTypes: [],
    requestStatuses: [],
  });

  readonly workflowTypesMap = workflowTypesMap;
  readonly workflowStatusesMap = workflowStatusesMap;
  readonly originalOrder = originalOrder;

  readonly formData = toSignal(this.filtersForm.valueChanges, {
    initialValue: this.filtersForm.value,
  });

  readonly currentPage = computed(() => this.state().currentPage);
  readonly workflowsHistory = computed(() => this.state().workflowsHistory?.requestDetails);
  readonly count = computed(() => this.state().totalItems);

  readonly currentPage$ = toObservable(this.currentPage);
  readonly formData$ = toObservable(this.formData);

  ngOnInit() {
    combineLatest([this.formData$, this.currentPage$]).subscribe(([formValues, page]) => {
      this.state.update((state) => ({
        ...state,
        requestTypes: formValues.requestTypes,
        requestStatuses: formValues.requestStatuses,
        currentPage: page,
      }));

      this.fetchAndSetWorkflows();
    });
  }

  handlePageChange(page: number) {
    if (page === this.state().currentPage) return;
    this.state.update((state) => ({ ...state, currentPage: page }));
  }

  private fetchAndSetWorkflows() {
    this.fetchWorkflows().subscribe({
      next: (results) => {
        this.state.update((state) => ({
          ...state,
          workflowsHistory: results.requestDetailsSearchResults,
          totalItems: results.requestDetailsSearchResults.total || 0,
        }));
      },
      error: (err) => {
        console.error('Error loading workflows', err);
      },
    });
  }

  private fetchWorkflows() {
    const requestSearchCriteria: RequestSearchCriteria = {
      resourceType: 'SECTOR_ASSOCIATION',
      resourceId: this.sectorId,
      requestTypes: this.state().requestTypes,
      requestStatuses: this.state().requestStatuses,
      historyCategory: HistoryCategory.SECTOR,
      pageNumber: this.state().currentPage - 1,
      pageSize: this.pageSize,
    };

    return this.requestsService
      .getRequestDetailsByResource(requestSearchCriteria)
      .pipe(map((requestDetailsSearchResults) => ({ requestDetailsSearchResults })));
  }
}
